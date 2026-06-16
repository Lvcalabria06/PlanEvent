package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemPrevisao;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.estoque.repository.PrevisaoConsumoRepository;
import domain.estoque.strategy.ContextoCalculoItem;
import domain.estoque.strategy.EstrategiaCalculoItemPrevisao;
import domain.estoque.strategy.FallbackParametrosPadraoStrategy;
import domain.estoque.strategy.MediaPonderadaComHistoricoStrategy;
import domain.estoque.strategy.RegistroHistoricoNormalizado;
import domain.estoque.strategy.ResultadoCalculoItem;
import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrevisaoConsumoServiceImpl implements PrevisaoConsumoService {

    private static final String PREVISAO_EM_GERACAO = "previsao-em-geracao";

    private final EventoRepository eventoRepository;
    private final ConsumoEventoRepository consumoEventoRepository;
    private final PrevisaoConsumoRepository previsaoConsumoRepository;
    private final List<EstrategiaCalculoItemPrevisao> estrategias;

    public PrevisaoConsumoServiceImpl(EventoRepository eventoRepository,
                                      ConsumoEventoRepository consumoEventoRepository,
                                      PrevisaoConsumoRepository previsaoConsumoRepository) {
        this(eventoRepository,
                consumoEventoRepository,
                previsaoConsumoRepository,
                List.of(new MediaPonderadaComHistoricoStrategy(), new FallbackParametrosPadraoStrategy()));
    }

    public PrevisaoConsumoServiceImpl(EventoRepository eventoRepository,
                                      ConsumoEventoRepository consumoEventoRepository,
                                      PrevisaoConsumoRepository previsaoConsumoRepository,
                                      List<EstrategiaCalculoItemPrevisao> estrategias) {
        this.eventoRepository = eventoRepository;
        this.consumoEventoRepository = consumoEventoRepository;
        this.previsaoConsumoRepository = previsaoConsumoRepository;
        if (estrategias == null || estrategias.isEmpty()) {
            throw new IllegalArgumentException("Pelo menos uma estrategia de calculo de previsao deve ser fornecida.");
        }
        this.estrategias = List.copyOf(estrategias);
    }

    @Override
    public PrevisaoConsumo gerarPrevisao(String eventoId, String usuarioId) {
        Evento eventoAtual = buscarEventoExistente(eventoId);
        BaseCalculo baseCalculo = montarBaseCalculo(eventoAtual);
        PrevisaoConsumo previsao = new PrevisaoConsumo(
                eventoAtual,
                usuarioId,
                baseCalculo.statusHistorico,
                baseCalculo.fallbackUtilizado,
                baseCalculo.totalEventosBase,
                baseCalculo.itens
        );
        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo ajustarPrevisao(String previsaoId, Map<String, Integer> quantidadesAjustadas, String usuarioId) {
        return ajustarPrevisao(previsaoId, quantidadesAjustadas, usuarioId, "Ajuste manual.");
    }

    @Override
    public PrevisaoConsumo ajustarPrevisao(String previsaoId, Map<String, Integer> quantidadesAjustadas, String usuarioId, String justificativa) {
        PrevisaoConsumo previsao = buscarPrevisaoExistente(previsaoId);
        previsao.ajustarQuantidades(quantidadesAjustadas, usuarioId, justificativa);
        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo recalcularPrevisao(String previsaoId, String usuarioId) {
        PrevisaoConsumo previsao = buscarPrevisaoExistente(previsaoId);
        Evento eventoAtual = buscarEventoExistente(previsao.getEventoId());
        BaseCalculo baseCalculo = montarBaseCalculo(eventoAtual);
        previsao.recalcular(
                eventoAtual,
                usuarioId,
                baseCalculo.statusHistorico,
                baseCalculo.fallbackUtilizado,
                baseCalculo.totalEventosBase,
                baseCalculo.itens
        );
        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo invalidarPrevisaoPorAlteracaoEvento(String eventoId, String usuarioId) {
        PrevisaoConsumo previsao = buscarPorEvento(eventoId);
        Evento eventoAtual = buscarEventoExistente(eventoId);
        previsao.invalidarPorAlteracaoEvento(eventoAtual, usuarioId);
        return previsaoConsumoRepository.salvar(previsao);
    }

    @Override
    public PrevisaoConsumo buscarPorEvento(String eventoId) {
        return previsaoConsumoRepository.buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Previsao nao encontrada para o evento informado."));
    }

    private BaseCalculo montarBaseCalculo(Evento eventoAtual) {
        List<RegistroHistoricoNormalizado> historicosValidos = consumoEventoRepository.listarTodos().stream()
                .filter(ConsumoEvento::isValido)
                .flatMap(consumo -> montarHistoricos(consumo, eventoAtual).stream())
                .collect(Collectors.toList());

        boolean fallbackInicial = historicosValidos.size() < 2;
        List<ItemPrevisao> itens = new ArrayList<>();

        Map<String, List<RegistroHistoricoNormalizado>> agrupados = historicosValidos.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getItemId() + "|" + h.getCategoria(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        if (agrupados.isEmpty()) {
            agrupados.put("fallback|global", List.of());
        }

        boolean algumFallback = fallbackInicial;
        for (Map.Entry<String, List<RegistroHistoricoNormalizado>> entry : agrupados.entrySet()) {
            String[] chave = entry.getKey().split("\\|");
            String itemId = chave[0];
            String categoria = chave.length > 1 ? chave[1] : chave[0];

            ContextoCalculoItem contexto = new ContextoCalculoItem(
                    PREVISAO_EM_GERACAO,
                    itemId,
                    categoria,
                    entry.getValue(),
                    historicosValidos
            );

            ResultadoCalculoItem resultado = selecionarEstrategia(contexto).calcular(contexto);
            itens.add(resultado.getItemPrevisao());
            if (resultado.isFallbackAplicado()) {
                algumFallback = true;
            }
        }

        return new BaseCalculo(
                algumFallback ? StatusHistoricoPrevisao.FALLBACK : StatusHistoricoPrevisao.SUFICIENTE,
                algumFallback,
                historicosValidos.size(),
                itens
        );
    }

    private EstrategiaCalculoItemPrevisao selecionarEstrategia(ContextoCalculoItem contexto) {
        return estrategias.stream()
                .filter(estrategia -> estrategia.aplicavel(contexto))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nenhuma estrategia de calculo de previsao aplicavel para o contexto."));
    }

    private List<RegistroHistoricoNormalizado> montarHistoricos(ConsumoEvento consumo, Evento eventoAtual) {
        Optional<Evento> historico = eventoRepository.buscarPorId(consumo.getEventoId());
        if (historico.isEmpty()) {
            return List.of();
        }
        Evento eventoHistorico = historico.get();
        if (!eventoHistorico.isConcluido() || eventoHistorico.getTipo() != eventoAtual.getTipo()) {
            return List.of();
        }

        double pesoSimilaridade = eventoHistorico.getPorte() == eventoAtual.getPorte() ? 1.0 : 0.7;
        double pesoRecencia = 1.0 / Math.max(1,
                Duration.between(eventoHistorico.getDataAtualizacao(), eventoAtual.getDataAtualizacao()).toDays() + 1);
        double fatorNormalizacao =
                (double) (eventoAtual.getQuantidadeEstimadaParticipantes() * duracaoHoras(eventoAtual))
                        / (eventoHistorico.getQuantidadeEstimadaParticipantes() * duracaoHoras(eventoHistorico));

        List<RegistroHistoricoNormalizado> registros = new ArrayList<>();
        for (ItemConsumoEvento item : consumo.getItensConsumidos()) {
            registros.add(new RegistroHistoricoNormalizado(
                    consumo.getEventoId(),
                    item.getItemEstoqueId(),
                    item.getCategoriaConsumo(),
                    item.getQuantidadeConsumida() * fatorNormalizacao,
                    pesoSimilaridade,
                    pesoRecencia,
                    fatorNormalizacao
            ));
        }
        return registros;
    }

    private long duracaoHoras(Evento evento) {
        if (evento.getJanelaInicioPlanejamento() != null && evento.getJanelaFimPlanejamento() != null) {
            return Math.max(Duration.between(evento.getJanelaInicioPlanejamento(), evento.getJanelaFimPlanejamento()).toHours(), 1);
        }
        return 1;
    }

    private Evento buscarEventoExistente(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado."));
    }

    private PrevisaoConsumo buscarPrevisaoExistente(String previsaoId) {
        return previsaoConsumoRepository.buscarPorId(previsaoId)
                .orElseThrow(() -> new IllegalArgumentException("Previsao nao encontrada."));
    }

    private static class BaseCalculo {
        private final StatusHistoricoPrevisao statusHistorico;
        private final boolean fallbackUtilizado;
        private final int totalEventosBase;
        private final List<ItemPrevisao> itens;

        private BaseCalculo(StatusHistoricoPrevisao statusHistorico,
                            boolean fallbackUtilizado,
                            int totalEventosBase,
                            List<ItemPrevisao> itens) {
            this.statusHistorico = statusHistorico;
            this.fallbackUtilizado = fallbackUtilizado;
            this.totalEventosBase = totalEventosBase;
            this.itens = itens;
        }
    }
}

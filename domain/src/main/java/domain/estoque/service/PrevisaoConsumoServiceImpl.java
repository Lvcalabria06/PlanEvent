package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemPrevisao;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.repository.ConsumoEventoRepository;
import domain.estoque.repository.PrevisaoConsumoRepository;
import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrevisaoConsumoServiceImpl implements PrevisaoConsumoService {

    private final EventoRepository eventoRepository;
    private final ConsumoEventoRepository consumoEventoRepository;
    private final PrevisaoConsumoRepository previsaoConsumoRepository;

    public PrevisaoConsumoServiceImpl(EventoRepository eventoRepository,
                                      ConsumoEventoRepository consumoEventoRepository,
                                      PrevisaoConsumoRepository previsaoConsumoRepository) {
        this.eventoRepository = eventoRepository;
        this.consumoEventoRepository = consumoEventoRepository;
        this.previsaoConsumoRepository = previsaoConsumoRepository;
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
        List<HistoricoConsumoNormalizado> historicosValidos = consumoEventoRepository.listarTodos().stream()
                .filter(ConsumoEvento::isValido)
                .map(consumo -> montarHistorico(consumo, eventoAtual))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        boolean fallback = historicosValidos.size() < 2;
        List<ItemPrevisao> itens = new ArrayList<>();

        Map<String, List<HistoricoConsumoNormalizado>> agrupados = historicosValidos.stream()
                .collect(Collectors.groupingBy(h -> h.itemId + "|" + h.categoria, LinkedHashMap::new, Collectors.toList()));

        if (agrupados.isEmpty()) {
            agrupados.put("fallback|global", List.of());
        }

        for (Map.Entry<String, List<HistoricoConsumoNormalizado>> entry : agrupados.entrySet()) {
            String[] chave = entry.getKey().split("\\|");
            String itemId = chave[0];
            String categoria = chave.length > 1 ? chave[1] : chave[0];
            List<HistoricoConsumoNormalizado> base = removerOutliers(entry.getValue());

            if (base.size() < 2) {
                fallback = true;
                double mediaGlobal = calcularMediaGlobalPorCategoria(historicosValidos, categoria);
                int estimada = (int) Math.round(mediaGlobal > 0 ? mediaGlobal : 10.0);
                itens.add(new ItemPrevisao(
                        "previsao-em-geracao",
                        itemId,
                        categoria,
                        estimada,
                        Math.max(0, (int) Math.floor(estimada * 0.9)),
                        (int) Math.ceil(estimada * 1.1),
                        "Fallback aplicado para categoria " + categoria + " por historico insuficiente."
                ));
                continue;
            }

            base.sort(Comparator.comparingDouble(HistoricoConsumoNormalizado::peso).reversed());
            double somaPesos = base.stream().mapToDouble(HistoricoConsumoNormalizado::peso).sum();
            double somaPonderada = base.stream().mapToDouble(h -> h.quantidadeNormalizada * h.peso).sum();
            int estimada = (int) Math.round(somaPonderada / somaPesos);

            List<Double> valores = base.stream().map(h -> h.quantidadeNormalizada).sorted().collect(Collectors.toList());
            int minimo = (int) Math.floor(percentil(valores, 0.25));
            int maximo = (int) Math.ceil(percentil(valores, 0.75));
            String explicacao = "Eventos usados: "
                    + base.stream().map(HistoricoConsumoNormalizado::eventoId).collect(Collectors.joining(", "))
                    + ". Media ponderada com normalizacao por porte/duracao.";

            itens.add(new ItemPrevisao(
                    "previsao-em-geracao",
                    itemId,
                    categoria,
                    estimada,
                    Math.max(0, minimo),
                    Math.max(estimada, maximo),
                    explicacao
            ));
        }

        return new BaseCalculo(
                fallback ? StatusHistoricoPrevisao.FALLBACK : StatusHistoricoPrevisao.SUFICIENTE,
                fallback,
                historicosValidos.size(),
                itens
        );
    }

    private Optional<HistoricoConsumoNormalizado> montarHistorico(ConsumoEvento consumo, Evento eventoAtual) {
        Optional<Evento> historico = eventoRepository.buscarPorId(consumo.getEventoId());
        if (historico.isEmpty()) {
            return Optional.empty();
        }
        Evento eventoHistorico = historico.get();
        if (!eventoHistorico.isConcluido() || eventoHistorico.getTipo() != eventoAtual.getTipo()) {
            return Optional.empty();
        }

        double pesoSimilaridade = eventoHistorico.getPorte() == eventoAtual.getPorte() ? 1.0 : 0.7;
        double pesoRecencia = 1.0 / Math.max(1, Duration.between(eventoHistorico.getDataAtualizacao(), eventoAtual.getDataAtualizacao()).toDays() + 1);
        double fatorNormalizacao = (double) (eventoAtual.getQuantidadeEstimadaParticipantes() * duracaoHoras(eventoAtual))
                / (eventoHistorico.getQuantidadeEstimadaParticipantes() * duracaoHoras(eventoHistorico));

        return consumo.getItensConsumidos().stream()
                .findFirst()
                .map(item -> new HistoricoConsumoNormalizado(
                        consumo.getEventoId(),
                        item.getItemEstoqueId(),
                        item.getCategoriaConsumo(),
                        item.getQuantidadeConsumida() * fatorNormalizacao,
                        pesoSimilaridade + pesoRecencia
                ));
    }

    private List<HistoricoConsumoNormalizado> removerOutliers(List<HistoricoConsumoNormalizado> base) {
        if (base.size() < 4) {
            return base;
        }
        List<Double> valores = base.stream().map(h -> h.quantidadeNormalizada).sorted().collect(Collectors.toList());
        double q1 = percentil(valores, 0.25);
        double q3 = percentil(valores, 0.75);
        double iqr = q3 - q1;
        double limiteSuperior = q3 + (1.5 * iqr);
        return base.stream()
                .filter(h -> h.quantidadeNormalizada <= limiteSuperior)
                .collect(Collectors.toList());
    }

    private double calcularMediaGlobalPorCategoria(List<HistoricoConsumoNormalizado> historicos, String categoria) {
        return historicos.stream()
                .filter(h -> h.categoria.equals(categoria))
                .mapToDouble(h -> h.quantidadeNormalizada)
                .average()
                .orElse(0.0);
    }

    private double percentil(List<Double> valores, double percentil) {
        if (valores.isEmpty()) {
            return 0.0;
        }
        int indice = (int) Math.floor((valores.size() - 1) * percentil);
        return valores.get(indice);
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

    private static class HistoricoConsumoNormalizado {
        private final String eventoId;
        private final String itemId;
        private final String categoria;
        private final double quantidadeNormalizada;
        private final double peso;

        private HistoricoConsumoNormalizado(String eventoId,
                                            String itemId,
                                            String categoria,
                                            double quantidadeNormalizada,
                                            double peso) {
            this.eventoId = eventoId;
            this.itemId = itemId;
            this.categoria = categoria;
            this.quantidadeNormalizada = quantidadeNormalizada;
            this.peso = peso;
        }

        private String eventoId() {
            return eventoId;
        }

        private double peso() {
            return peso;
        }
    }
}

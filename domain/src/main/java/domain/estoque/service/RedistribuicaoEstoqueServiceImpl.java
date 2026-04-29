package domain.estoque.service;

import domain.estoque.entity.AlocacaoRedistribuicao;
import domain.estoque.entity.CenarioRedistribuicao;
import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.CenarioRedistribuicaoRepository;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.valueobject.CriticidadeEvento;
import domain.estoque.valueobject.StatusRedistribuicao;
import domain.estoque.valueobject.StatusReservaEstoque;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RedistribuicaoEstoqueServiceImpl implements RedistribuicaoEstoqueService {

    private final ReservaEstoqueRepository reservaEstoqueRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final EventoRepository eventoRepository;
    private final CenarioRedistribuicaoRepository cenarioRedistribuicaoRepository;

    public RedistribuicaoEstoqueServiceImpl(ReservaEstoqueRepository reservaEstoqueRepository,
                                             ItemEstoqueRepository itemEstoqueRepository,
                                             EventoRepository eventoRepository,
                                             CenarioRedistribuicaoRepository cenarioRedistribuicaoRepository) {
        this.reservaEstoqueRepository = reservaEstoqueRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.eventoRepository = eventoRepository;
        this.cenarioRedistribuicaoRepository = cenarioRedistribuicaoRepository;
    }

    @Override
    public CenarioRedistribuicao gerarCenarioRedistribuicao(String usuarioId,
                                                             LocalDateTime periodoInicio,
                                                             LocalDateTime periodoFim) {
        validarParametros(usuarioId, periodoInicio, periodoFim);

        List<ReservaEstoque> reservasNoPeriodo = obterReservasAtivasNoPeriodo(periodoInicio, periodoFim);

        if (reservasNoPeriodo.isEmpty()) {
            throw new IllegalStateException("Nao existem reservas ativas no periodo informado.");
        }

        Map<String, Evento> eventosMap = carregarEventos(reservasNoPeriodo);
        Map<String, Double> prioridadesPorEvento = calcularPrioridades(eventosMap, periodoInicio);

        List<ReservaEstoque> reservasRedistribuiveis = reservasNoPeriodo.stream()
                .filter(r -> podeRedistribuir(r))
                .collect(Collectors.toList());

        List<ReservaEstoque> reservasImutaveis = reservasNoPeriodo.stream()
                .filter(r -> !podeRedistribuir(r))
                .collect(Collectors.toList());

        List<AlocacaoRedistribuicao> alocacoesAtuais = montarAlocacoesAtuais(reservasNoPeriodo);
        Map<String, Integer> estoqueDisponivel = calcularEstoqueDisponivel(reservasImutaveis);

        List<AlocacaoRedistribuicao> alocacoesOtimizadas = calcularDistribuicaoOtimizada(
                reservasRedistribuiveis, estoqueDisponivel, prioridadesPorEvento);

        for (ReservaEstoque reservaImutavel : reservasImutaveis) {
            for (ItemReserva item : reservaImutavel.getItensReservados()) {
                alocacoesOtimizadas.add(new AlocacaoRedistribuicao(
                        reservaImutavel.getEventoId(),
                        item.getItemEstoqueId(),
                        item.getQuantidade(),
                        item.getQuantidade()
                ));
            }
        }

        CenarioRedistribuicao cenario = new CenarioRedistribuicao(
                usuarioId, periodoInicio, periodoFim, alocacoesAtuais, alocacoesOtimizadas
        );

        invalidarCenariosPendentesAnteriores(usuarioId);

        return cenarioRedistribuicaoRepository.salvar(cenario);
    }

    @Override
    public CenarioRedistribuicao aplicarRedistribuicao(String cenarioId, String usuarioId) {
        CenarioRedistribuicao cenario = buscarCenarioExistente(cenarioId);

        if (!cenario.isPendente()) {
            throw new IllegalStateException("Apenas cenarios pendentes podem ser aplicados.");
        }

        aplicarAlocacoesNasReservas(cenario);
        cenario.aplicar(usuarioId);

        return cenarioRedistribuicaoRepository.salvar(cenario);
    }

    @Override
    public CenarioRedistribuicao invalidarCenario(String cenarioId, String usuarioId, String motivo) {
        CenarioRedistribuicao cenario = buscarCenarioExistente(cenarioId);
        cenario.invalidar(usuarioId, motivo);
        return cenarioRedistribuicaoRepository.salvar(cenario);
    }

    @Override
    public CenarioRedistribuicao buscarCenario(String cenarioId) {
        return buscarCenarioExistente(cenarioId);
    }

    private List<AlocacaoRedistribuicao> calcularDistribuicaoOtimizada(
            List<ReservaEstoque> reservasRedistribuiveis,
            Map<String, Integer> estoqueDisponivel,
            Map<String, Double> prioridadesPorEvento) {

        Map<String, List<DemandaEvento>> demandasPorItem = new LinkedHashMap<>();
        for (ReservaEstoque reserva : reservasRedistribuiveis) {
            for (ItemReserva item : reserva.getItensReservados()) {
                demandasPorItem.computeIfAbsent(item.getItemEstoqueId(), k -> new ArrayList<>())
                        .add(new DemandaEvento(
                                reserva.getEventoId(),
                                item.getItemEstoqueId(),
                                item.getQuantidade(),
                                prioridadesPorEvento.getOrDefault(reserva.getEventoId(), 0.0)
                        ));
            }
        }

        List<AlocacaoRedistribuicao> alocacoes = new ArrayList<>();
        Map<String, Integer> estoqueRestante = new HashMap<>(estoqueDisponivel);

        for (Map.Entry<String, List<DemandaEvento>> entry : demandasPorItem.entrySet()) {
            String itemId = entry.getKey();
            List<DemandaEvento> demandas = entry.getValue();
            int disponivel = estoqueRestante.getOrDefault(itemId, 0);
            int demandaTotal = demandas.stream().mapToInt(d -> d.quantidade).sum();

            demandas.sort(Comparator.comparingDouble(DemandaEvento::getPrioridade).reversed());

            if (demandaTotal <= disponivel) {
                for (DemandaEvento demanda : demandas) {
                    alocacoes.add(new AlocacaoRedistribuicao(
                            demanda.eventoId, demanda.itemEstoqueId,
                            demanda.quantidade, demanda.quantidade));
                }
                estoqueRestante.put(itemId, disponivel - demandaTotal);
            } else {
                int restante = disponivel;

                Map<String, Integer> alocadoPorEvento = new LinkedHashMap<>();
                for (DemandaEvento demanda : demandas) {
                    int minimo = (int) Math.ceil(demanda.quantidade * 0.5);
                    int alocado = Math.min(minimo, restante);
                    alocadoPorEvento.put(demanda.eventoId, alocado);
                    restante -= alocado;
                }

                for (DemandaEvento demanda : demandas) {
                    if (restante <= 0) break;
                    int jaAlocado = alocadoPorEvento.get(demanda.eventoId);
                    int faltante = demanda.quantidade - jaAlocado;
                    int complemento = Math.min(faltante, restante);
                    alocadoPorEvento.put(demanda.eventoId, jaAlocado + complemento);
                    restante -= complemento;
                }

                for (DemandaEvento demanda : demandas) {
                    int alocado = alocadoPorEvento.get(demanda.eventoId);
                    int deficit = demanda.quantidade - alocado;

                    AlocacaoRedistribuicao alocacao = new AlocacaoRedistribuicao(
                            demanda.eventoId, demanda.itemEstoqueId,
                            demanda.quantidade, alocado);

                    if (deficit > 0) {
                        Optional<ItemSubstituicao> substituicao = buscarSubstituicaoDisponivel(
                                itemId, deficit, estoqueRestante);
                        if (substituicao.isPresent()) {
                            ItemSubstituicao sub = substituicao.get();
                            int qtdSubstituto = (int) Math.ceil(deficit * sub.getFatorEquivalencia());
                            int disponivelSub = estoqueRestante.getOrDefault(sub.getItemSubstitutoId(), 0);
                            int alocadoSub = Math.min(qtdSubstituto, disponivelSub);
                            if (alocadoSub > 0) {
                                alocacao.aplicarSubstituicao(sub.getItemSubstitutoId(), alocadoSub);
                                estoqueRestante.put(sub.getItemSubstitutoId(), disponivelSub - alocadoSub);
                            }
                        }
                    }

                    alocacoes.add(alocacao);
                }

                estoqueRestante.put(itemId, restante);
            }
        }

        return alocacoes;
    }

    private Map<String, Double> calcularPrioridades(Map<String, Evento> eventosMap,
                                                     LocalDateTime referencia) {
        Map<String, Double> prioridades = new HashMap<>();
        for (Map.Entry<String, Evento> entry : eventosMap.entrySet()) {
            Evento evento = entry.getValue();
            double pesoCriticidade = calcularPesoCriticidade(determinarCriticidade(evento));
            double pesoProximidade = calcularPesoProximidade(evento, referencia);
            double pesoPorte = calcularPesoPorte(evento.getPorte());
            prioridades.put(entry.getKey(), pesoCriticidade * 0.5 + pesoProximidade * 0.3 + pesoPorte * 0.2);
        }
        return prioridades;
    }

    private CriticidadeEvento determinarCriticidade(Evento evento) {
        if (evento.isPlanejamentoConfirmado()) {
            return evento.getPorte() == PorteEvento.MEGA || evento.getPorte() == PorteEvento.GRANDE
                    ? CriticidadeEvento.CRITICA : CriticidadeEvento.ALTA;
        }
        return evento.getPorte() == PorteEvento.GRANDE || evento.getPorte() == PorteEvento.MEGA
                ? CriticidadeEvento.MEDIA : CriticidadeEvento.BAIXA;
    }

    private double calcularPesoCriticidade(CriticidadeEvento criticidade) {
        return switch (criticidade) {
            case CRITICA -> 1.0;
            case ALTA -> 0.75;
            case MEDIA -> 0.5;
            case BAIXA -> 0.25;
        };
    }

    private double calcularPesoProximidade(Evento evento, LocalDateTime referencia) {
        if (evento.getJanelaInicioPlanejamento() == null) {
            return 0.5;
        }
        long diasAteEvento = Duration.between(referencia, evento.getJanelaInicioPlanejamento()).toDays();
        if (diasAteEvento <= 0) return 1.0;
        if (diasAteEvento <= 7) return 0.9;
        if (diasAteEvento <= 30) return 0.6;
        return 0.3;
    }

    private double calcularPesoPorte(PorteEvento porte) {
        return switch (porte) {
            case MEGA -> 1.0;
            case GRANDE -> 0.75;
            case MEDIO -> 0.5;
            case PEQUENO -> 0.25;
        };
    }

    private List<ReservaEstoque> obterReservasAtivasNoPeriodo(LocalDateTime periodoInicio, LocalDateTime periodoFim) {
        return reservaEstoqueRepository.listarTodas().stream()
                .filter(r -> statusAtivo(r.getStatus()))
                .filter(r -> r.sobrepoePeriodo(periodoInicio, periodoFim))
                .collect(Collectors.toList());
    }

    private boolean podeRedistribuir(ReservaEstoque reserva) {
        return reserva.getStatus() == StatusReservaEstoque.PENDENTE
                || reserva.getStatus() == StatusReservaEstoque.CONFIRMADA;
    }

    private boolean statusAtivo(StatusReservaEstoque status) {
        return status == StatusReservaEstoque.PENDENTE
                || status == StatusReservaEstoque.CONFIRMADA
                || status == StatusReservaEstoque.EM_USO;
    }

    private Map<String, Evento> carregarEventos(List<ReservaEstoque> reservas) {
        Map<String, Evento> eventosMap = new HashMap<>();
        for (ReservaEstoque reserva : reservas) {
            if (!eventosMap.containsKey(reserva.getEventoId())) {
                eventoRepository.buscarPorId(reserva.getEventoId())
                        .ifPresent(evento -> eventosMap.put(reserva.getEventoId(), evento));
            }
        }
        return eventosMap;
    }

    private List<AlocacaoRedistribuicao> montarAlocacoesAtuais(List<ReservaEstoque> reservas) {
        List<AlocacaoRedistribuicao> alocacoes = new ArrayList<>();
        for (ReservaEstoque reserva : reservas) {
            for (ItemReserva item : reserva.getItensReservados()) {
                alocacoes.add(new AlocacaoRedistribuicao(
                        reserva.getEventoId(),
                        item.getItemEstoqueId(),
                        item.getQuantidade(),
                        item.getQuantidade()
                ));
            }
        }
        return alocacoes;
    }

    private Map<String, Integer> calcularEstoqueDisponivel(List<ReservaEstoque> reservasImutaveis) {
        Map<String, Integer> estoque = new HashMap<>();
        for (ItemEstoque item : itemEstoqueRepository.listarTodos()) {
            estoque.put(item.getNome(), item.getQuantidadeTotal());
        }

        for (ReservaEstoque reserva : reservasImutaveis) {
            for (ItemReserva item : reserva.getItensReservados()) {
                estoque.merge(item.getItemEstoqueId(), -item.getQuantidade(), Integer::sum);
            }
        }

        return estoque;
    }

    private Optional<ItemSubstituicao> buscarSubstituicaoDisponivel(String itemId,
                                                                      int quantidadeNecessaria,
                                                                      Map<String, Integer> estoqueRestante) {
        List<ItemSubstituicao> substituicoes = itemEstoqueRepository.buscarSubstituicoesPorItem(itemId);
        for (ItemSubstituicao sub : substituicoes) {
            int disponivelSub = estoqueRestante.getOrDefault(sub.getItemSubstitutoId(), 0);
            int necessario = (int) Math.ceil(quantidadeNecessaria * sub.getFatorEquivalencia());
            if (disponivelSub >= necessario) {
                return Optional.of(sub);
            }
        }
        return substituicoes.stream()
                .filter(sub -> estoqueRestante.getOrDefault(sub.getItemSubstitutoId(), 0) > 0)
                .findFirst();
    }

    private void aplicarAlocacoesNasReservas(CenarioRedistribuicao cenario) {
        Map<String, List<AlocacaoRedistribuicao>> alocacoesPorEvento = cenario.getAlocacoesOtimizadas().stream()
                .collect(Collectors.groupingBy(AlocacaoRedistribuicao::getEventoId));

        List<ReservaEstoque> todasReservas = reservaEstoqueRepository.listarTodas();

        for (Map.Entry<String, List<AlocacaoRedistribuicao>> entry : alocacoesPorEvento.entrySet()) {
            String eventoId = entry.getKey();
            List<AlocacaoRedistribuicao> alocacoes = entry.getValue();

            Optional<ReservaEstoque> reservaOpt = todasReservas.stream()
                    .filter(r -> r.getEventoId().equals(eventoId))
                    .filter(this::podeRedistribuir)
                    .filter(r -> r.sobrepoePeriodo(cenario.getPeriodoInicio(), cenario.getPeriodoFim()))
                    .findFirst();

            if (reservaOpt.isPresent()) {
                ReservaEstoque reserva = reservaOpt.get();
                List<ItemReserva> novosItens = new ArrayList<>();
                for (AlocacaoRedistribuicao alocacao : alocacoes) {
                    if (alocacao.getQuantidadeRedistribuida() > 0) {
                        novosItens.add(new ItemReserva(
                                reserva.getId(),
                                alocacao.getItemEstoqueId(),
                                alocacao.getQuantidadeRedistribuida()
                        ));
                    }
                    if (alocacao.possuiSubstituicao()) {
                        novosItens.add(new ItemReserva(
                                reserva.getId(),
                                alocacao.getItemSubstitutoId(),
                                alocacao.getQuantidadeSubstituto()
                        ));
                    }
                }
                if (!novosItens.isEmpty()) {
                    reserva.atualizarSolicitacao(reserva.getDataInicio(), reserva.getDataFim(), novosItens);
                    reservaEstoqueRepository.salvar(reserva);
                }
            }
        }
    }

    private void invalidarCenariosPendentesAnteriores(String usuarioId) {
        for (CenarioRedistribuicao cenarioPendente : cenarioRedistribuicaoRepository.listarPendentes()) {
            cenarioPendente.invalidar(usuarioId, "Novo cenario gerado, cenario anterior invalidado.");
            cenarioRedistribuicaoRepository.salvar(cenarioPendente);
        }
    }

    private CenarioRedistribuicao buscarCenarioExistente(String cenarioId) {
        return cenarioRedistribuicaoRepository.buscarPorId(cenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Cenario de redistribuicao nao encontrado."));
    }

    private void validarParametros(String usuarioId, LocalDateTime periodoInicio, LocalDateTime periodoFim) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuario e obrigatorio.");
        }
        if (periodoInicio == null || periodoFim == null || periodoInicio.isAfter(periodoFim)) {
            throw new IllegalArgumentException("Periodo de redistribuicao invalido.");
        }
    }

    private static class DemandaEvento {
        private final String eventoId;
        private final String itemEstoqueId;
        private final int quantidade;
        private final double prioridade;

        private DemandaEvento(String eventoId, String itemEstoqueId, int quantidade, double prioridade) {
            this.eventoId = eventoId;
            this.itemEstoqueId = itemEstoqueId;
            this.quantidade = quantidade;
            this.prioridade = prioridade;
        }

        private double getPrioridade() {
            return prioridade;
        }
    }
}

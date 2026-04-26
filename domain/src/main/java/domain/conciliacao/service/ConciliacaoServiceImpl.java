package domain.conciliacao.service;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.entity.VinculoConciliacao;
import domain.conciliacao.repository.RelatorioConciliacaoRepository;
import domain.conciliacao.repository.VinculoConciliacaoRepository;
import domain.conciliacao.valueobject.ItemRelatorioConciliacao;
import domain.conciliacao.valueobject.MetodoConciliacao;
import domain.conciliacao.valueobject.StatusConciliacao;
import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.StatusContrato;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConciliacaoServiceImpl implements ConciliacaoService {

    private final ContratoRepository contratoRepository;
    private final DespesaRepository despesaRepository;
    private final VinculoConciliacaoRepository vinculoRepository;
    private final RelatorioConciliacaoRepository relatorioRepository;

    public ConciliacaoServiceImpl(
            ContratoRepository contratoRepository,
            DespesaRepository despesaRepository,
            VinculoConciliacaoRepository vinculoRepository,
            RelatorioConciliacaoRepository relatorioRepository) {
        this.contratoRepository = contratoRepository;
        this.despesaRepository = despesaRepository;
        this.vinculoRepository = vinculoRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Override
    public void executarConciliacaoAutomatica(String eventoId, String responsavelId) {
        List<Despesa> despesasElegiveis = despesaRepository.listarPorEventoId(eventoId)
                .stream()
                .filter(d -> d.getValor().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        List<Contrato> contratosAtivos = contratoRepository.listarPorEventoId(eventoId)
                .stream()
                .filter(this::isContratoAtivoParaConciliacao)
                .collect(Collectors.toList());

        Map<String, BigDecimal> valorPorDespesa = despesasElegiveis.stream()
                .collect(Collectors.toMap(Despesa::getId, Despesa::getValor));

        for (Despesa despesa : despesasElegiveis) {
            List<Contrato> compativeis = contratosAtivos.stream()
                    .filter(c -> isCompativel(c, despesa))
                    .collect(Collectors.toList());

            if (compativeis.isEmpty()) {
                vinculoRepository.removerPorDespesaId(despesa.getId());
                continue;
            }

            Contrato escolhido = selecionarPreferencial(compativeis, despesa, valorPorDespesa);

            Optional<VinculoConciliacao> existente = vinculoRepository.buscarPorDespesaId(despesa.getId());
            if (existente.isPresent()) {
                existente.get().substituirVinculo(escolhido.getId(), responsavelId, MetodoConciliacao.AUTOMATICO);
                vinculoRepository.salvar(existente.get());
            } else {
                vinculoRepository.salvar(new VinculoConciliacao(
                        eventoId, despesa.getId(), escolhido.getId(),
                        MetodoConciliacao.AUTOMATICO, responsavelId));
            }
        }
    }

    @Override
    public List<Despesa> listarDespesasDescobertasPorEvento(String eventoId) {
        List<String> despesasVinculadas = vinculoRepository.listarPorEventoId(eventoId)
                .stream()
                .map(VinculoConciliacao::getDespesaId)
                .collect(Collectors.toList());

        return despesaRepository.listarPorEventoId(eventoId)
                .stream()
                .filter(d -> d.getValor().compareTo(BigDecimal.ZERO) > 0)
                .filter(d -> !despesasVinculadas.contains(d.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Contrato> listarContratosExtrapoladosPorEvento(String eventoId) {
        List<Contrato> contratos = contratoRepository.listarPorEventoId(eventoId);

        Map<String, BigDecimal> totalPorContrato = calcularTotalConciliadoPorContrato(eventoId);

        return contratos.stream()
                .filter(c -> {
                    BigDecimal totalConciliado = totalPorContrato.getOrDefault(c.getId(), BigDecimal.ZERO);
                    return totalConciliado.compareTo(c.getValor()) > 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public VinculoConciliacao vincularManualmente(String despesaId, String contratoId, String responsavelId) {
        Despesa despesa = despesaRepository.buscarPorId(despesaId)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada."));

        if (despesa.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Despesas com valor inválido não participam da conciliação.");
        }

        Contrato contrato = contratoRepository.buscarPorId(contratoId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado."));

        if (!contrato.getEventoId().equals(despesa.getEventoId())) {
            throw new IllegalStateException("O contrato deve pertencer ao mesmo evento da despesa.");
        }

        if (!isContratoAtivoParaConciliacao(contrato)) {
            throw new IllegalStateException("Contrato encerrado ou cancelado não pode ser usado na conciliação.");
        }

        if (!isVigenteNaData(contrato, despesa.getData())) {
            throw new IllegalStateException("O contrato não está vigente na data da despesa.");
        }

        Optional<VinculoConciliacao> existente = vinculoRepository.buscarPorDespesaId(despesaId);
        if (existente.isPresent()) {
            existente.get().substituirVinculo(contratoId, responsavelId, MetodoConciliacao.MANUAL);
            return vinculoRepository.salvar(existente.get());
        }

        return vinculoRepository.salvar(new VinculoConciliacao(
                despesa.getEventoId(), despesaId, contratoId, MetodoConciliacao.MANUAL, responsavelId));
    }

    @Override
    public RelatorioConciliacao gerarRelatorio(String eventoId, String responsavelId) {
        List<Despesa> despesas = despesaRepository.listarPorEventoId(eventoId)
                .stream()
                .filter(d -> d.getValor().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (despesas.isEmpty()) {
            throw new IllegalStateException("Não há despesas elegíveis para gerar o relatório.");
        }

        Map<String, VinculoConciliacao> vinculoPorDespesa = vinculoRepository.listarPorEventoId(eventoId)
                .stream()
                .collect(Collectors.toMap(VinculoConciliacao::getDespesaId, v -> v));

        List<ItemRelatorioConciliacao> itens = new ArrayList<>();
        for (Despesa despesa : despesas) {
            VinculoConciliacao vinculo = vinculoPorDespesa.get(despesa.getId());
            if (vinculo != null) {
                itens.add(new ItemRelatorioConciliacao(
                        despesa.getId(), vinculo.getContratoId(),
                        StatusConciliacao.COBERTA, vinculo.getMetodo()));
            } else {
                itens.add(new ItemRelatorioConciliacao(
                        despesa.getId(), null, StatusConciliacao.DESCOBERTA, null));
            }
        }

        RelatorioConciliacao relatorio = new RelatorioConciliacao(eventoId, responsavelId, itens);
        return relatorioRepository.salvar(relatorio);
    }

    private boolean isContratoAtivoParaConciliacao(Contrato contrato) {
        return contrato.getStatus() != StatusContrato.ENCERRADO
                && contrato.getStatus() != StatusContrato.CANCELADO;
    }

    private boolean isVigenteNaData(Contrato contrato, LocalDateTime data) {
        return !data.isBefore(contrato.getDataInicio()) && !data.isAfter(contrato.getDataFim());
    }

    private boolean isCompativel(Contrato contrato, Despesa despesa) {
        if (!isVigenteNaData(contrato, despesa.getData())) {
            return false;
        }
        if (contrato.getFornecedorId() != null
                && !contrato.getFornecedorId().equals(despesa.getFornecedorId())) {
            return false;
        }
        return true;
    }

    private Contrato selecionarPreferencial(List<Contrato> compativeis, Despesa despesa,
            Map<String, BigDecimal> valorPorDespesa) {
        Map<String, BigDecimal> totalConciliadoLocal = compativeis.stream()
                .collect(Collectors.toMap(
                        Contrato::getId,
                        c -> valorConciliadoAtual(c, valorPorDespesa)));

        return compativeis.stream()
                .sorted(Comparator
                        .comparing((Contrato c) -> categoriaExata(c, despesa) ? 0 : 1)
                        .thenComparing(c -> c.getValor().subtract(
                                totalConciliadoLocal.getOrDefault(c.getId(), BigDecimal.ZERO))))
                .findFirst()
                .orElseThrow();
    }

    private boolean categoriaExata(Contrato contrato, Despesa despesa) {
        return contrato.getTipo() != null
                && contrato.getTipo().name().equalsIgnoreCase(despesa.getCategoria().name());
    }

    private BigDecimal valorConciliadoAtual(Contrato contrato, Map<String, BigDecimal> valorPorDespesa) {
        return vinculoRepository.listarPorContratoId(contrato.getId())
                .stream()
                .map(v -> valorPorDespesa.getOrDefault(v.getDespesaId(), BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, BigDecimal> calcularTotalConciliadoPorContrato(String eventoId) {
        List<VinculoConciliacao> vinculos = vinculoRepository.listarPorEventoId(eventoId);
        Map<String, BigDecimal> valorPorDespesaNoEvento = despesaRepository.listarPorEventoId(eventoId)
                .stream()
                .collect(Collectors.toMap(Despesa::getId, Despesa::getValor));

        return vinculos.stream()
                .collect(Collectors.groupingBy(
                        VinculoConciliacao::getContratoId,
                        Collectors.reducing(BigDecimal.ZERO,
                                v -> valorPorDespesaNoEvento.getOrDefault(v.getDespesaId(), BigDecimal.ZERO),
                                BigDecimal::add)));
    }
}

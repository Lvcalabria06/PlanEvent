package domain.financeiro.service;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.exception.CategoriaOrcamentoEsgotadaException;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.DesvioOrcamentario;
import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DespesaServiceImpl implements DespesaService {

    private static final BigDecimal PERCENTUAL_BLOQUEIO  = new BigDecimal("1.00");
    private static final BigDecimal PERCENTUAL_APROVACAO = new BigDecimal("0.80");

    private final DespesaRepository despesaRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private final EventoRepository eventoRepository;
    private final FornecedorRepository fornecedorRepository;

    public DespesaServiceImpl(DespesaRepository despesaRepository,
                               OrcamentoEventoRepository orcamentoEventoRepository,
                               CategoriaOrcamentoRepository categoriaOrcamentoRepository,
                               EventoRepository eventoRepository,
                               FornecedorRepository fornecedorRepository) {
        this.despesaRepository = despesaRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
        this.eventoRepository = eventoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public Despesa registrarDespesa(Despesa despesa) {
        eventoRepository.buscarPorId(despesa.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));

        Fornecedor fornecedor = fornecedorRepository.buscarPorId(despesa.getFornecedorId())
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor inválido ou não encontrado."));
        if (!fornecedor.isAtivo()) {
            throw new IllegalArgumentException("Fornecedor inativo não pode ser vinculado a despesas.");
        }

        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(despesa.getEventoId())
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. Cadastre o orçamento antes de registrar despesas."));

        CategoriaOrcamento categoriaOrc = categoriaOrcamentoRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), despesa.getCategoria())
                .orElseThrow(() -> new IllegalStateException(
                        "Categoria '" + despesa.getCategoria()
                                + "' não possui orçamento previsto cadastrado para este evento."));

        BigDecimal totalAtivo = despesaRepository
                .somarValoresAtivosPorEventoECategoria(despesa.getEventoId(), despesa.getCategoria());
        if (totalAtivo == null) totalAtivo = BigDecimal.ZERO;

        BigDecimal previsto  = categoriaOrc.getValorPrevisto();
        BigDecimal novoTotal = totalAtivo.add(despesa.getValor());

        if (novoTotal.compareTo(previsto.multiply(PERCENTUAL_BLOQUEIO)) > 0) {
            throw new CategoriaOrcamentoEsgotadaException(
                    "Despesa bloqueada: o total acumulado de " + totalAtivo
                            + " mais o novo valor de " + despesa.getValor()
                            + " ultrapassa o orçamento previsto de " + previsto
                            + " para a categoria '" + despesa.getCategoria() + "'.");
        }

        if (novoTotal.compareTo(previsto.multiply(PERCENTUAL_APROVACAO)) >= 0) {
            despesa.marcarPendente();
        }

        return despesaRepository.salvar(despesa);
    }

    @Override
    public Despesa buscarDespesa(String id) {
        return despesaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada."));
    }

    @Override
    public List<Despesa> listarDespesasPorEvento(String eventoId) {
        return despesaRepository.listarPorEventoId(eventoId);
    }

    @Override
    public DesvioOrcamentario calcularDesvio(String eventoId, CategoriaDespesa categoria) {
        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento."));

        CategoriaOrcamento categoriaOrcamento = categoriaOrcamentoRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), categoria)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento para a categoria '" + categoria + "' não encontrado."));

        BigDecimal totalAtivo = despesaRepository
                .somarValoresAtivosPorEventoECategoria(eventoId, categoria);

        return new DesvioOrcamentario(
                categoria,
                categoriaOrcamento.getValorPrevisto(),
                totalAtivo != null ? totalAtivo : BigDecimal.ZERO);
    }

    @Override
    public List<DesvioOrcamentario> calcularDesviosPorEvento(String eventoId) {
        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento."));

        List<CategoriaOrcamento> categorias = categoriaOrcamentoRepository
                .listarPorOrcamentoId(orcamento.getId());

        List<DesvioOrcamentario> desvios = new ArrayList<>();
        for (CategoriaOrcamento cat : categorias) {
            BigDecimal totalAtivo = despesaRepository
                    .somarValoresAtivosPorEventoECategoria(eventoId, cat.getNome());

            desvios.add(new DesvioOrcamentario(
                    cat.getNome(),
                    cat.getValorPrevisto(),
                    totalAtivo != null ? totalAtivo : BigDecimal.ZERO));
        }
        return desvios;
    }

    @Override
    public Despesa aprovarDespesa(String despesaId, String aprovadorId) {
        Despesa despesa = despesaRepository.buscarPorId(despesaId)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada."));
        despesa.aprovar(aprovadorId);
        return despesaRepository.salvar(despesa);
    }

    @Override
    public Despesa rejeitarDespesa(String despesaId, String aprovadorId, String motivo) {
        Despesa despesa = despesaRepository.buscarPorId(despesaId)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada."));
        despesa.rejeitar(aprovadorId, motivo);
        return despesaRepository.salvar(despesa);
    }
}

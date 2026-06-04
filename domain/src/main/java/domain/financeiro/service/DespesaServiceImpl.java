package domain.financeiro.service;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.template.ProcessadorAtualizacaoDespesaTemplateMethod;
import domain.financeiro.template.ProcessadorRegistroDespesaTemplateMethod;
import domain.financeiro.template.ValidadorLimitePadraoTemplateMethod;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.DesvioOrcamentario;
import domain.fornecedor.repository.FornecedorRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DespesaServiceImpl implements DespesaService {

    private final DespesaRepository despesaRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private final EventoRepository eventoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ValidadorLimitePadraoTemplateMethod validadorLimite;

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
        this.validadorLimite = new ValidadorLimitePadraoTemplateMethod(
                despesaRepository, orcamentoEventoRepository, categoriaOrcamentoRepository);
    }

    @Override
    public Despesa registrarDespesa(Despesa despesa) {
        ProcessadorRegistroDespesaTemplateMethod processador = new ProcessadorRegistroDespesaTemplateMethod(
                eventoRepository,
                fornecedorRepository,
                validadorLimite,
                despesaRepository);
        return processador.executar(despesa, BigDecimal.ZERO, despesa.getValor());
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
    public List<Despesa> pesquisarPorCategoria(String eventoId, CategoriaDespesa categoria) {
        return despesaRepository.listarPorEventoECategoria(eventoId, categoria);
    }

    @Override
    public List<Despesa> pesquisarPorFornecedor(String eventoId, String fornecedorId) {
        if (fornecedorId == null || fornecedorId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do fornecedor é obrigatório para pesquisa.");
        }
        return despesaRepository.listarPorEventoEFornecedor(eventoId, fornecedorId);
    }

    @Override
    public Despesa atualizarDespesa(String despesaId, BigDecimal novoValor, LocalDateTime novaData) {
        if (novoValor == null || novoValor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Novo valor da despesa deve ser maior que zero.");
        }

        Despesa despesa = buscarDespesa(despesaId);
        BigDecimal valorAnterior = despesa.getValor();

        ProcessadorAtualizacaoDespesaTemplateMethod processador =
                new ProcessadorAtualizacaoDespesaTemplateMethod(
                        eventoRepository,
                        fornecedorRepository,
                        validadorLimite,
                        despesaRepository,
                        novaData);

        return processador.executar(despesa, valorAnterior, novoValor);
    }

    @Override
    public void excluirDespesa(String despesaId) {
        Despesa despesa = buscarDespesa(despesaId);
        despesa.garantirPodeSerAlterada();
        despesaRepository.excluir(despesaId);
    }

    @Override
    public DesvioOrcamentario calcularDesvio(String eventoId, CategoriaDespesa categoria) {
        CategoriaOrcamento categoriaOrcamento = obterCategoriaOrcamento(eventoId, categoria);

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
        Despesa despesa = buscarDespesa(despesaId);
        despesa.aprovar(aprovadorId);
        return despesaRepository.salvar(despesa);
    }

    @Override
    public Despesa rejeitarDespesa(String despesaId, String aprovadorId, String motivo) {
        Despesa despesa = buscarDespesa(despesaId);
        despesa.rejeitar(aprovadorId, motivo);
        return despesaRepository.salvar(despesa);
    }

    private CategoriaOrcamento obterCategoriaOrcamento(String eventoId, CategoriaDespesa categoria) {
        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento."));

        return categoriaOrcamentoRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), categoria)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento para a categoria '" + categoria + "' não encontrado."));
    }
}

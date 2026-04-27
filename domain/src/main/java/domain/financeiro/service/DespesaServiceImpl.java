package domain.financeiro.service;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.DesvioOrcamentario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class DespesaServiceImpl implements DespesaService {

    private final DespesaRepository despesaRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private final EventoRepository eventoRepository;

    public DespesaServiceImpl(DespesaRepository despesaRepository,
                               OrcamentoEventoRepository orcamentoEventoRepository,
                               CategoriaOrcamentoRepository categoriaOrcamentoRepository,
                               EventoRepository eventoRepository) {
        this.despesaRepository = despesaRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Despesa registrarDespesa(Despesa despesa) {
        eventoRepository.buscarPorId(despesa.getEventoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));

        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(despesa.getEventoId())
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. Cadastre o orçamento antes de registrar despesas."));

        categoriaOrcamentoRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), despesa.getCategoria())
                .orElseThrow(() -> new IllegalStateException(
                        "Categoria '" + despesa.getCategoria()
                                + "' não possui orçamento previsto cadastrado para este evento."));

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

        BigDecimal totalRealizado = despesaRepository
                .somarValoresPorEventoECategoria(eventoId, categoria);

        return new DesvioOrcamentario(
                categoria,
                categoriaOrcamento.getValorPrevisto(),
                totalRealizado != null ? totalRealizado : BigDecimal.ZERO);
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
            BigDecimal totalRealizado = despesaRepository
                    .somarValoresPorEventoECategoria(eventoId, cat.getNome());

            desvios.add(new DesvioOrcamentario(
                    cat.getNome(),
                    cat.getValorPrevisto(),
                    totalRealizado != null ? totalRealizado : BigDecimal.ZERO));
        }
        return desvios;
    }
}

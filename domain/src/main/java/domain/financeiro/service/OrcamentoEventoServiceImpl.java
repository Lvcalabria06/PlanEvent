package domain.financeiro.service;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.util.List;

public class OrcamentoEventoServiceImpl implements OrcamentoEventoService {

    private final OrcamentoEventoRepository orcamentoRepository;
    private final CategoriaOrcamentoRepository categoriaRepository;
    private final EventoRepository eventoRepository;

    public OrcamentoEventoServiceImpl(OrcamentoEventoRepository orcamentoRepository,
                                       CategoriaOrcamentoRepository categoriaRepository,
                                       EventoRepository eventoRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public OrcamentoEvento criarOrcamento(String eventoId, BigDecimal valorTotal) {
        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
        if (orcamentoRepository.buscarPorEventoId(eventoId).isPresent()) {
            throw new IllegalStateException("Já existe orçamento cadastrado para este evento.");
        }
        return orcamentoRepository.salvar(new OrcamentoEvento(eventoId, valorTotal));
    }

    @Override
    public OrcamentoEvento buscarPorEvento(String eventoId) {
        return orcamentoRepository.buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Orçamento não encontrado para o evento."));
    }

    @Override
    public CategoriaOrcamento adicionarCategoria(String eventoId,
                                                  CategoriaDespesa categoria,
                                                  BigDecimal valorPrevisto) {
        OrcamentoEvento orcamento = buscarPorEvento(eventoId);
        if (categoriaRepository.buscarPorOrcamentoECategoria(orcamento.getId(), categoria).isPresent()) {
            throw new IllegalStateException(
                    "Categoria '" + categoria + "' já possui orçamento cadastrado.");
        }
        return categoriaRepository.salvar(
                new CategoriaOrcamento(orcamento.getId(), categoria, valorPrevisto));
    }

    @Override
    public CategoriaOrcamento atualizarCategoria(String eventoId,
                                                  CategoriaDespesa categoria,
                                                  BigDecimal novoValor) {
        OrcamentoEvento orcamento = buscarPorEvento(eventoId);
        CategoriaOrcamento cat = categoriaRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), categoria)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoria '" + categoria + "' não possui orçamento cadastrado."));
        cat.ajustarValorPrevisto(novoValor);
        return categoriaRepository.salvar(cat);
    }

    @Override
    public List<CategoriaOrcamento> listarCategorias(String eventoId) {
        OrcamentoEvento orcamento = buscarPorEvento(eventoId);
        return categoriaRepository.listarPorOrcamentoId(orcamento.getId());
    }
}

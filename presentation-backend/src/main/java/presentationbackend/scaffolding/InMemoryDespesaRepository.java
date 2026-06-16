package presentationbackend.scaffolding;

import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Stub em memória para DespesaRepository enquanto o módulo financeiro não possui
 * persistência real. Retorna coleções vazias para que FornecedorServiceImpl
 * possa ser instanciado sem dependências ausentes.
 *
 * <p>Substituído automaticamente pelo adapter JPA quando o módulo financeiro
 * fornecer sua implementação (via {@code @ConditionalOnMissingBean}).</p>
 */
class InMemoryDespesaRepository implements DespesaRepository {

    @Override
    public Despesa salvar(Despesa despesa) {
        return despesa;
    }

    @Override
    public Optional<Despesa> buscarPorId(String id) {
        return Optional.empty();
    }

    @Override
    public List<Despesa> listarPorEventoId(String eventoId) {
        return List.of();
    }

    @Override
    public List<Despesa> listarPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return List.of();
    }

    @Override
    public BigDecimal somarValoresPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal somarValoresAtivosPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return BigDecimal.ZERO;
    }

    @Override
    public List<Despesa> listarPorFornecedorId(String fornecedorId) {
        return List.of();
    }

    @Override
    public List<Despesa> listarPorEventoEFornecedor(String eventoId, String fornecedorId) {
        return List.of();
    }
}

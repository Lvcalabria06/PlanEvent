package presentationbackend.scaffolding;

import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.StatusDespesa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Stub em memória para DespesaRepository enquanto o módulo financeiro não possui
 * persistência real. Retorna coleções vazias para que FornecedorServiceImpl
 * possa ser instanciado sem dependências ausentes.
 *
 * <p>Substituído automaticamente pelo adapter JPA quando o módulo financeiro
 * fornecer sua implementação.</p>
 */
public class InMemoryDespesaRepository implements DespesaRepository {

    private final Map<String, Despesa> porId = new ConcurrentHashMap<>();

    @Override
    public Despesa salvar(Despesa despesa) {
        porId.put(despesa.getId(), despesa);
        return despesa;
    }

    @Override
    public void excluir(String id) {
        porId.remove(id);
    }

    @Override
    public Optional<Despesa> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<Despesa> listarPorEventoId(String eventoId) {
        return porId.values().stream()
                .filter(d -> d.getEventoId().equals(eventoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Despesa> listarPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return porId.values().stream()
                .filter(d -> d.getEventoId().equals(eventoId) && d.getCategoria() == categoria)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal somarValoresPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return listarPorEventoECategoria(eventoId, categoria).stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal somarValoresAtivosPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return listarPorEventoECategoria(eventoId, categoria).stream()
                .filter(d -> d.getStatus() != StatusDespesa.REJEITADA)
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Despesa> listarPorEventoEFornecedor(String eventoId, String fornecedorId) {
        return porId.values().stream()
                .filter(d -> d.getEventoId().equals(eventoId) && d.getFornecedorId().equals(fornecedorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Despesa> listarPorFornecedorId(String fornecedorId) {
        return porId.values().stream()
                .filter(d -> d.getFornecedorId().equals(fornecedorId))
                .collect(Collectors.toList());
    }
}

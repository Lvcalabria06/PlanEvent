package infrastructure.persistence.memory;

import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.StatusDespesa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryDespesaRepository implements DespesaRepository {

    private final Map<String, Despesa> porId = new ConcurrentHashMap<>();

    @Override
    public Despesa salvar(Despesa despesa) {
        porId.put(despesa.getId(), despesa);
        return despesa;
    }

    @Override
    public Optional<Despesa> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public void excluir(String id) {
        porId.remove(id);
    }

    @Override
    public List<Despesa> listarPorEventoId(String eventoId) {
        return porId.values().stream()
                .filter(d -> eventoId.equals(d.getEventoId()))
                .toList();
    }

    @Override
    public List<Despesa> listarPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return porId.values().stream()
                .filter(d -> eventoId.equals(d.getEventoId()) && d.getCategoria() == categoria)
                .toList();
    }

    @Override
    public List<Despesa> listarPorEventoEFornecedor(String eventoId, String fornecedorId) {
        return porId.values().stream()
                .filter(d -> eventoId.equals(d.getEventoId()) && fornecedorId.equals(d.getFornecedorId()))
                .toList();
    }

    @Override
    public List<Despesa> listarPorFornecedorId(String fornecedorId) {
        return porId.values().stream()
                .filter(d -> fornecedorId.equals(d.getFornecedorId()))
                .toList();
    }

    @Override
    public BigDecimal somarValoresPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return somarAtivos(eventoId, categoria, false);
    }

    @Override
    public BigDecimal somarValoresAtivosPorEventoECategoria(String eventoId, CategoriaDespesa categoria) {
        return somarAtivos(eventoId, categoria, true);
    }

    private BigDecimal somarAtivos(String eventoId, CategoriaDespesa categoria, boolean somenteAtivos) {
        return porId.values().stream()
                .filter(d -> eventoId.equals(d.getEventoId()) && d.getCategoria() == categoria)
                .filter(d -> !somenteAtivos || isAtiva(d.getStatus()))
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static boolean isAtiva(StatusDespesa status) {
        return status == StatusDespesa.REGISTRADA
                || status == StatusDespesa.PENDENTE_APROVACAO
                || status == StatusDespesa.APROVADA;
    }

    public void limpar() {
        porId.clear();
    }
}

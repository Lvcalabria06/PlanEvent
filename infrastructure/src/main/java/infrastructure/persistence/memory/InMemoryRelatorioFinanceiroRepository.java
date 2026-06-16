package infrastructure.persistence.memory;

import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryRelatorioFinanceiroRepository implements RelatorioFinanceiroRepository {

    private final Map<String, RelatorioFinanceiro> porId = new ConcurrentHashMap<>();

    @Override
    public RelatorioFinanceiro salvar(RelatorioFinanceiro relatorio) {
        porId.put(relatorio.getId(), relatorio);
        return relatorio;
    }

    @Override
    public Optional<RelatorioFinanceiro> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<RelatorioFinanceiro> listarPorEventoId(String eventoId) {
        return porId.values().stream()
                .filter(r -> eventoId.equals(r.getEventoId()))
                .sorted(Comparator.comparing(RelatorioFinanceiro::getDataGeracao))
                .toList();
    }

    public void limpar() {
        porId.clear();
    }
}

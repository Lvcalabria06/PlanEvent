package infrastructure.persistence.memory;

import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.repository.SimulacaoRelatorioRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemorySimulacaoRelatorioRepository implements SimulacaoRelatorioRepository {

    private final Map<String, SimulacaoRelatorioFinanceiro> porId = new ConcurrentHashMap<>();

    @Override
    public SimulacaoRelatorioFinanceiro salvar(SimulacaoRelatorioFinanceiro simulacao) {
        porId.put(simulacao.getId(), simulacao);
        return simulacao;
    }

    @Override
    public Optional<SimulacaoRelatorioFinanceiro> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public void remover(String id) {
        porId.remove(id);
    }

    public void limpar() {
        porId.clear();
    }
}

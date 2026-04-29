package domain.estoque.repository;

import domain.estoque.entity.CenarioRedistribuicao;

import java.util.List;
import java.util.Optional;

public interface CenarioRedistribuicaoRepository {
    CenarioRedistribuicao salvar(CenarioRedistribuicao cenario);
    Optional<CenarioRedistribuicao> buscarPorId(String id);
    List<CenarioRedistribuicao> listarPendentes();
    List<CenarioRedistribuicao> listarTodos();
}

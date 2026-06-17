package application.estoque.usecase;

import application.estoque.dto.AplicarRedistribuicaoRequest;
import application.estoque.dto.CenarioRedistribuicaoResponse;
import application.estoque.dto.GerarCenarioRedistribuicaoRequest;
import application.estoque.dto.InvalidarCenarioRequest;

import java.util.List;

public interface RedistribuicaoEstoqueUseCase {

    CenarioRedistribuicaoResponse gerar(GerarCenarioRedistribuicaoRequest request);

    CenarioRedistribuicaoResponse aplicar(String id, AplicarRedistribuicaoRequest request);

    CenarioRedistribuicaoResponse invalidar(String id, InvalidarCenarioRequest request);

    CenarioRedistribuicaoResponse buscar(String id);

    List<CenarioRedistribuicaoResponse> listarPendentes();

    List<CenarioRedistribuicaoResponse> listarTodos();
}

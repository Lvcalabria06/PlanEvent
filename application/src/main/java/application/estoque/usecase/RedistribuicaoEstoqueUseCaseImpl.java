package application.estoque.usecase;

import application.estoque.dto.AplicarRedistribuicaoRequest;
import application.estoque.dto.CenarioRedistribuicaoResponse;
import application.estoque.dto.GerarCenarioRedistribuicaoRequest;
import application.estoque.dto.InvalidarCenarioRequest;
import application.estoque.mapper.EstoqueDtoMapper;
import domain.estoque.entity.CenarioRedistribuicao;
import domain.estoque.service.RedistribuicaoEstoqueService;

import java.util.List;

public class RedistribuicaoEstoqueUseCaseImpl implements RedistribuicaoEstoqueUseCase {

    private final RedistribuicaoEstoqueService redistribuicaoEstoqueService;

    public RedistribuicaoEstoqueUseCaseImpl(RedistribuicaoEstoqueService redistribuicaoEstoqueService) {
        this.redistribuicaoEstoqueService = redistribuicaoEstoqueService;
    }

    @Override
    public CenarioRedistribuicaoResponse gerar(GerarCenarioRedistribuicaoRequest request) {
        CenarioRedistribuicao cenario = redistribuicaoEstoqueService.gerarCenarioRedistribuicao(
                request.usuarioId(),
                request.periodoInicio(),
                request.periodoFim());
        return EstoqueDtoMapper.paraResposta(cenario);
    }

    @Override
    public CenarioRedistribuicaoResponse aplicar(String id, AplicarRedistribuicaoRequest request) {
        CenarioRedistribuicao cenario = redistribuicaoEstoqueService.aplicarRedistribuicao(id, request.usuarioId());
        return EstoqueDtoMapper.paraResposta(cenario);
    }

    @Override
    public CenarioRedistribuicaoResponse invalidar(String id, InvalidarCenarioRequest request) {
        CenarioRedistribuicao cenario = redistribuicaoEstoqueService.invalidarCenario(
                id,
                request.usuarioId(),
                request.motivo());
        return EstoqueDtoMapper.paraResposta(cenario);
    }

    @Override
    public CenarioRedistribuicaoResponse buscar(String id) {
        CenarioRedistribuicao cenario = redistribuicaoEstoqueService.buscarCenario(id);
        return EstoqueDtoMapper.paraResposta(cenario);
    }

    @Override
    public List<CenarioRedistribuicaoResponse> listarPendentes() {
        return redistribuicaoEstoqueService.listarPendentes().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<CenarioRedistribuicaoResponse> listarTodos() {
        return redistribuicaoEstoqueService.listarTodos().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }
}

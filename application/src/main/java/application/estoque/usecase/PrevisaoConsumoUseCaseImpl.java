package application.estoque.usecase;

import application.estoque.dto.AjustarPrevisaoRequest;
import application.estoque.dto.GerarPrevisaoRequest;
import application.estoque.dto.PrevisaoConsumoResponse;
import application.estoque.mapper.EstoqueDtoMapper;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.service.PrevisaoConsumoService;

import java.util.List;

public class PrevisaoConsumoUseCaseImpl implements PrevisaoConsumoUseCase {

    private final PrevisaoConsumoService previsaoConsumoService;

    public PrevisaoConsumoUseCaseImpl(PrevisaoConsumoService previsaoConsumoService) {
        this.previsaoConsumoService = previsaoConsumoService;
    }

    @Override
    public PrevisaoConsumoResponse gerar(GerarPrevisaoRequest request) {
        PrevisaoConsumo previsao = previsaoConsumoService.gerarPrevisao(request.eventoId(), request.usuarioId());
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public PrevisaoConsumoResponse ajustar(String id, AjustarPrevisaoRequest request) {
        PrevisaoConsumo previsao = previsaoConsumoService.ajustarPrevisao(
                id,
                request.quantidadesAjustadas(),
                request.usuarioId(),
                request.justificativa());
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public PrevisaoConsumoResponse recalcular(String id, String usuarioId) {
        PrevisaoConsumo previsao = previsaoConsumoService.recalcularPrevisao(id, usuarioId);
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public PrevisaoConsumoResponse invalidarPorEvento(String eventoId, String usuarioId) {
        PrevisaoConsumo previsao = previsaoConsumoService.invalidarPrevisaoPorAlteracaoEvento(eventoId, usuarioId);
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public PrevisaoConsumoResponse buscarPorEvento(String eventoId) {
        PrevisaoConsumo previsao = previsaoConsumoService.buscarPorEvento(eventoId);
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public PrevisaoConsumoResponse buscar(String id) {
        PrevisaoConsumo previsao = previsaoConsumoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Previsao nao encontrada."));
        return EstoqueDtoMapper.paraResposta(previsao);
    }

    @Override
    public List<PrevisaoConsumoResponse> listarTodas() {
        return previsaoConsumoService.listarTodas().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }
}

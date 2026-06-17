package application.estoque.usecase;

import application.estoque.dto.AjustarPrevisaoRequest;
import application.estoque.dto.GerarPrevisaoRequest;
import application.estoque.dto.PrevisaoConsumoResponse;

import java.util.List;

public interface PrevisaoConsumoUseCase {

    PrevisaoConsumoResponse gerar(GerarPrevisaoRequest request);

    PrevisaoConsumoResponse ajustar(String id, AjustarPrevisaoRequest request);

    PrevisaoConsumoResponse recalcular(String id, String usuarioId);

    PrevisaoConsumoResponse invalidarPorEvento(String eventoId, String usuarioId);

    PrevisaoConsumoResponse buscarPorEvento(String eventoId);

    PrevisaoConsumoResponse buscar(String id);

    List<PrevisaoConsumoResponse> listarTodas();
}

package application.estoque.usecase;

import application.estoque.dto.ConsumoEventoResponse;
import application.estoque.dto.RegistrarConsumoEventoRequest;

import java.util.List;

public interface ConsumoEventoUseCase {

    ConsumoEventoResponse registrar(RegistrarConsumoEventoRequest request);

    void invalidar(String id);

    ConsumoEventoResponse buscar(String id);

    List<ConsumoEventoResponse> listarPorEvento(String eventoId);

    List<ConsumoEventoResponse> listarTodos();
}

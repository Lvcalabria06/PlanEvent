package application.estoque.usecase;

import application.estoque.dto.AtualizarReservaEstoqueRequest;
import application.estoque.dto.CriarReservaEstoqueRequest;
import application.estoque.dto.ReservaEstoqueResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaEstoqueUseCase {

    ReservaEstoqueResponse criar(CriarReservaEstoqueRequest request);

    ReservaEstoqueResponse editar(String id, AtualizarReservaEstoqueRequest request);

    ReservaEstoqueResponse confirmar(String id);

    ReservaEstoqueResponse iniciarUso(String id);

    ReservaEstoqueResponse finalizar(String id);

    void cancelar(String id);

    ReservaEstoqueResponse buscar(String id);

    List<ReservaEstoqueResponse> listarTodas();

    List<ReservaEstoqueResponse> listarPorEvento(String eventoId);

    List<ReservaEstoqueResponse> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
}

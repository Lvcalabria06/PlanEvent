package application.contrato.usecase;

import application.contrato.dto.ContratoResponse;
import application.contrato.dto.CriarContratoRequest;
import application.contrato.dto.EditarContratoRequest;

import java.util.List;

public interface ContratoUseCase {

    ContratoResponse criar(CriarContratoRequest request);

    ContratoResponse editar(String id, EditarContratoRequest request);

    ContratoResponse buscar(String id);

    List<ContratoResponse> listar();

    List<ContratoResponse> listarPorEvento(String eventoId);

    ContratoResponse encerrar(String id);
}

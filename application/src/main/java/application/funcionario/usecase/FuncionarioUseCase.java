package application.funcionario.usecase;

import application.funcionario.dto.CadastrarFuncionarioRequest;
import application.funcionario.dto.EditarFuncionarioRequest;
import application.funcionario.dto.FuncionarioResponse;

import java.util.List;

/**
 * Casos de uso de gerenciamento de funcionários, orquestrando o serviço de domínio
 * e expondo DTOs à camada de apresentação.
 */
public interface FuncionarioUseCase {

    FuncionarioResponse cadastrar(CadastrarFuncionarioRequest request);

    FuncionarioResponse editar(String funcionarioId, EditarFuncionarioRequest request);

    FuncionarioResponse buscar(String funcionarioId);

    List<FuncionarioResponse> listar();

    void inativar(String funcionarioId);

    List<FuncionarioResponse> filtrar(String expressao);
}

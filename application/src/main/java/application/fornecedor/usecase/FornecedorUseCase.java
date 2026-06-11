package application.fornecedor.usecase;

import application.fornecedor.dto.CadastrarFornecedorRequest;
import application.fornecedor.dto.EditarFornecedorRequest;
import application.fornecedor.dto.FornecedorResponse;

import java.util.List;

public interface FornecedorUseCase {

    FornecedorResponse cadastrar(CadastrarFornecedorRequest request);

    FornecedorResponse editar(String id, EditarFornecedorRequest request);

    FornecedorResponse buscar(String id);

    List<FornecedorResponse> listar();

    void desativar(String id);
}

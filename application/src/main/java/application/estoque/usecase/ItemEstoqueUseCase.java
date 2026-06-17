package application.estoque.usecase;

import application.estoque.dto.AdicionarEstoqueRequest;
import application.estoque.dto.CadastrarItemEstoqueRequest;
import application.estoque.dto.CadastrarSubstituicaoRequest;
import application.estoque.dto.EditarItemEstoqueRequest;
import application.estoque.dto.ItemEstoqueResponse;
import application.estoque.dto.ItemSubstituicaoResponse;

import java.util.List;

public interface ItemEstoqueUseCase {

    ItemEstoqueResponse cadastrar(CadastrarItemEstoqueRequest request);

    ItemEstoqueResponse editar(String id, EditarItemEstoqueRequest request);

    void desativar(String id);

    void reativar(String id);

    ItemEstoqueResponse adicionarEstoque(String id, AdicionarEstoqueRequest request);

    ItemEstoqueResponse buscar(String id);

    List<ItemEstoqueResponse> listarTodos();

    List<ItemEstoqueResponse> listarAtivos();

    ItemSubstituicaoResponse cadastrarSubstituicao(CadastrarSubstituicaoRequest request);

    List<ItemSubstituicaoResponse> listarSubstituicoes();
}

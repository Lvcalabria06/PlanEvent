package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;

import java.util.List;
import java.util.Optional;

public interface ItemEstoqueService {

    ItemEstoque cadastrar(String nome, int quantidadeTotal);

    ItemEstoque editar(String id, String nome, int quantidadeTotal);

    void desativar(String id);

    void reativar(String id);

    ItemEstoque adicionarEstoque(String id, int quantidade);

    Optional<ItemEstoque> buscarPorId(String id);

    List<ItemEstoque> listarTodos();

    List<ItemEstoque> listarAtivos();

    ItemSubstituicao registrarSubstituicao(String itemOriginalId, String itemSubstitutoId, double fatorEquivalencia);

    List<ItemSubstituicao> listarSubstituicoes();
}

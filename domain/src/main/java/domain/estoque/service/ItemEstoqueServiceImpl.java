package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.repository.ItemEstoqueRepository;

import java.util.List;
import java.util.Optional;

public class ItemEstoqueServiceImpl implements ItemEstoqueService {

    private final ItemEstoqueRepository itemEstoqueRepository;

    public ItemEstoqueServiceImpl(ItemEstoqueRepository itemEstoqueRepository) {
        this.itemEstoqueRepository = itemEstoqueRepository;
    }

    @Override
    public ItemEstoque cadastrar(String nome, int quantidadeTotal) {
        ItemEstoque item = new ItemEstoque(nome, quantidadeTotal);
        return itemEstoqueRepository.salvar(item);
    }

    @Override
    public ItemEstoque editar(String id, String nome, int quantidadeTotal) {
        ItemEstoque item = exigirExistente(id);
        item.atualizarDados(nome, quantidadeTotal);
        return itemEstoqueRepository.salvar(item);
    }

    @Override
    public void desativar(String id) {
        ItemEstoque item = exigirExistente(id);
        item.desativar();
        itemEstoqueRepository.salvar(item);
    }

    @Override
    public void reativar(String id) {
        ItemEstoque item = exigirExistente(id);
        item.reativar();
        itemEstoqueRepository.salvar(item);
    }

    @Override
    public ItemEstoque adicionarEstoque(String id, int quantidade) {
        ItemEstoque item = exigirExistente(id);
        item.adicionarEstoque(quantidade);
        return itemEstoqueRepository.salvar(item);
    }

    @Override
    public Optional<ItemEstoque> buscarPorId(String id) {
        return itemEstoqueRepository.buscarPorId(id);
    }

    @Override
    public List<ItemEstoque> listarTodos() {
        return itemEstoqueRepository.listarTodos();
    }

    @Override
    public List<ItemEstoque> listarAtivos() {
        return itemEstoqueRepository.listarAtivos();
    }

    @Override
    public ItemSubstituicao registrarSubstituicao(String itemOriginalId, String itemSubstitutoId,
            double fatorEquivalencia) {
        exigirExistente(itemOriginalId);
        exigirExistente(itemSubstitutoId);
        return itemEstoqueRepository.salvarSubstituicao(
                new ItemSubstituicao(itemOriginalId, itemSubstitutoId, fatorEquivalencia));
    }

    @Override
    public List<ItemSubstituicao> listarSubstituicoes() {
        return itemEstoqueRepository.listarSubstituicoes();
    }

    private ItemEstoque exigirExistente(String id) {
        return itemEstoqueRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de estoque nao encontrado."));
    }
}

package domain.estoque.repository;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;

import java.util.List;
import java.util.Optional;

public interface ItemEstoqueRepository {
    Optional<ItemEstoque> buscarPorId(String id);
    List<ItemEstoque> listarTodos();
    List<ItemSubstituicao> buscarSubstituicoesPorItem(String itemOriginalId);
}

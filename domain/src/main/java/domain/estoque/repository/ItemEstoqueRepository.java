package domain.estoque.repository;

import domain.estoque.entity.ItemEstoque;

import java.util.Optional;

public interface ItemEstoqueRepository {
    Optional<ItemEstoque> buscarPorId(String id);
}

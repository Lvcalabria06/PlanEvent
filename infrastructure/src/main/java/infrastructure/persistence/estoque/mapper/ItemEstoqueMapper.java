package infrastructure.persistence.estoque.mapper;

import domain.estoque.entity.ItemEstoque;
import infrastructure.persistence.estoque.entity.ItemEstoqueJpaEntity;

public final class ItemEstoqueMapper {

    private ItemEstoqueMapper() {
    }

    public static ItemEstoqueJpaEntity paraJpa(ItemEstoque item) {
        return new ItemEstoqueJpaEntity(
                item.getId(),
                item.getNome(),
                item.getQuantidadeTotal(),
                item.getQuantidadeDisponivel(),
                item.isAtivo(),
                item.getDataCriacao(),
                item.getDataAtualizacao());
    }

    public static ItemEstoque paraDominio(ItemEstoqueJpaEntity entity) {
        return ItemEstoque.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getQuantidadeTotal(),
                entity.getQuantidadeDisponivel(),
                entity.isAtivo(),
                entity.getDataCriacao(),
                entity.getDataAtualizacao());
    }
}

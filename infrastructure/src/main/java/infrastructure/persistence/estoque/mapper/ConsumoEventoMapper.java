package infrastructure.persistence.estoque.mapper;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import infrastructure.persistence.estoque.entity.ConsumoEventoJpaEntity;
import infrastructure.persistence.estoque.entity.ItemConsumoEventoJpaEntity;

import java.util.List;

public final class ConsumoEventoMapper {

    private ConsumoEventoMapper() {
    }

    public static ConsumoEventoJpaEntity paraJpa(ConsumoEvento consumo) {
        ConsumoEventoJpaEntity entity = new ConsumoEventoJpaEntity(
                consumo.getId(),
                consumo.getEventoId(),
                consumo.getRegistradoPorUsuarioId(),
                consumo.getDataRegistro(),
                consumo.isValido());

        if (consumo.getItensConsumidos() != null) {
            List<ItemConsumoEventoJpaEntity> itens = consumo.getItensConsumidos().stream()
                    .map(item -> new ItemConsumoEventoJpaEntity(
                            item.getId(),
                            item.getItemEstoqueId(),
                            item.getCategoriaConsumo(),
                            item.getQuantidadeConsumida(),
                            entity))
                    .toList();
            entity.setItensConsumidos(itens);
        }

        return entity;
    }

    public static ConsumoEvento paraDominio(ConsumoEventoJpaEntity entity) {
        List<ItemConsumoEvento> itens = List.of();
        if (entity.getItensConsumidos() != null) {
            itens = entity.getItensConsumidos().stream()
                    .map(item -> ItemConsumoEvento.reconstituir(
                            item.getId(),
                            item.getItemEstoqueId(),
                            item.getCategoriaConsumo(),
                            item.getQuantidadeConsumida()))
                    .toList();
        }

        return ConsumoEvento.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getRegistradoPorUsuarioId(),
                entity.getDataRegistro(),
                itens,
                entity.isValido());
    }
}

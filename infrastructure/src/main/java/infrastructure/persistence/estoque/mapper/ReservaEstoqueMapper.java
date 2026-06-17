package infrastructure.persistence.estoque.mapper;

import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import infrastructure.persistence.estoque.entity.ItemReservaJpaEntity;
import infrastructure.persistence.estoque.entity.ReservaEstoqueJpaEntity;

import java.util.List;

public final class ReservaEstoqueMapper {

    private ReservaEstoqueMapper() {
    }

    public static ReservaEstoqueJpaEntity paraJpa(ReservaEstoque reserva) {
        ReservaEstoqueJpaEntity entity = new ReservaEstoqueJpaEntity(
                reserva.getId(),
                reserva.getEventoId(),
                reserva.getDataInicio(),
                reserva.getDataFim(),
                reserva.getStatus());

        if (reserva.getItensReservados() != null) {
            List<ItemReservaJpaEntity> itens = reserva.getItensReservados().stream()
                    .map(item -> new ItemReservaJpaEntity(
                            item.getId(),
                            item.getItemEstoqueId(),
                            item.getQuantidade(),
                            entity))
                    .toList();
            entity.setItensReservados(itens);
        }

        return entity;
    }

    public static ReservaEstoque paraDominio(ReservaEstoqueJpaEntity entity) {
        List<ItemReserva> itens = List.of();
        if (entity.getItensReservados() != null) {
            itens = entity.getItensReservados().stream()
                    .map(item -> ItemReserva.reconstituir(
                            item.getId(),
                            entity.getId(),
                            item.getItemEstoqueId(),
                            item.getQuantidade()))
                    .toList();
        }

        return ReservaEstoque.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                itens);
    }
}

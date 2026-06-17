package infrastructure.persistence.local.mapper;

import domain.local.entity.Local;
import infrastructure.persistence.local.entity.LocalJpaEntity;

public final class LocalMapper {

    private LocalMapper() {}

    public static Local paraDominio(LocalJpaEntity entity) {
        return Local.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getCapacidade(),
                entity.getEndereco(),
                entity.getTipo(),
                entity.getInfraestrutura(),
                entity.getRestricoes(),
                entity.getCusto(),
                entity.getStatus(),
                entity.getUpdatedAt()
        );
    }

    public static LocalJpaEntity paraJpa(Local local) {
        return new LocalJpaEntity(
                local.getId(),
                local.getNome(),
                local.getCapacidade(),
                local.getEndereco(),
                local.getTipo(),
                local.getInfraestrutura(),
                local.getRestricoes(),
                local.getCusto(),
                local.getStatus(),
                local.getUpdatedAt()
        );
    }
}

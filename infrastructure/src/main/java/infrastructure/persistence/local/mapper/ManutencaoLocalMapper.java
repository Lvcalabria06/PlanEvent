package infrastructure.persistence.local.mapper;

import domain.local.entity.ManutencaoLocal;
import infrastructure.persistence.local.entity.ManutencaoLocalJpaEntity;

public final class ManutencaoLocalMapper {

    private ManutencaoLocalMapper() {}

    public static ManutencaoLocal paraDominio(ManutencaoLocalJpaEntity entity) {
        return ManutencaoLocal.reconstituir(
                entity.getId(),
                entity.getLocalId(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getResponsavel(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static ManutencaoLocalJpaEntity paraJpa(ManutencaoLocal manutencao) {
        return new ManutencaoLocalJpaEntity(
                manutencao.getId(),
                manutencao.getLocalId(),
                manutencao.getDataInicio(),
                manutencao.getDataFim(),
                manutencao.getResponsavel(),
                manutencao.getCreatedAt(),
                manutencao.getUpdatedAt()
        );
    }
}

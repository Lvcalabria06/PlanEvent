package infrastructure.persistence.conciliacao.mapper;

import domain.conciliacao.entity.VinculoConciliacao;
import infrastructure.persistence.conciliacao.entity.VinculoConciliacaoJpaEntity;

public final class VinculoConciliacaoMapper {

    private VinculoConciliacaoMapper() {}

    public static VinculoConciliacao paraDominio(VinculoConciliacaoJpaEntity entity) {
        return VinculoConciliacao.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getDespesaId(),
                entity.getContratoId(),
                entity.getMetodo(),
                entity.getResponsavelId(),
                entity.getDataConciliacao(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static VinculoConciliacaoJpaEntity paraJpa(VinculoConciliacao vinculo) {
        return new VinculoConciliacaoJpaEntity(
                vinculo.getId(),
                vinculo.getEventoId(),
                vinculo.getDespesaId(),
                vinculo.getContratoId(),
                vinculo.getMetodo(),
                vinculo.getResponsavelId(),
                vinculo.getDataConciliacao(),
                vinculo.getCreatedAt(),
                vinculo.getUpdatedAt()
        );
    }
}

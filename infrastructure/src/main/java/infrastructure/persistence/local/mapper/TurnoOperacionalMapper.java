package infrastructure.persistence.local.mapper;

import domain.local.turno.entity.TurnoOperacional;
import infrastructure.persistence.local.entity.TurnoOperacionalJpaEntity;

public final class TurnoOperacionalMapper {

    private TurnoOperacionalMapper() {}

    public static TurnoOperacional paraDominio(TurnoOperacionalJpaEntity entity) {
        return TurnoOperacional.reconstituir(
                entity.getId(),
                entity.getLocalId(),
                entity.getNome(),
                entity.getHoraInicio(),
                entity.getHoraFim(),
                entity.getDiasDaSemana(),
                entity.getStatus(),
                entity.getCapacidade(),
                entity.getObservacoes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static TurnoOperacionalJpaEntity paraJpa(TurnoOperacional turno) {
        return new TurnoOperacionalJpaEntity(
                turno.getId(),
                turno.getLocalId(),
                turno.getNome(),
                turno.getHoraInicio(),
                turno.getHoraFim(),
                turno.getDiasDaSemana(),
                turno.getStatus(),
                turno.getCapacidade(),
                turno.getObservacoes(),
                turno.getCreatedAt(),
                turno.getUpdatedAt()
        );
    }
}

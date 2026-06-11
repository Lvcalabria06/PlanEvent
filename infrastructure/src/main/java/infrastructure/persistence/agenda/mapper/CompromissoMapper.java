package infrastructure.persistence.agenda.mapper;

import domain.agenda.entity.Compromisso;
import infrastructure.persistence.agenda.entity.CompromissoJpaEntity;

public final class CompromissoMapper {

    private CompromissoMapper() {
    }

    public static CompromissoJpaEntity paraJpa(Compromisso compromisso) {
        return new CompromissoJpaEntity(
                compromisso.getId(),
                compromisso.getGestorId(),
                compromisso.getEventoId(),
                compromisso.getTitulo(),
                compromisso.getDescricao(),
                compromisso.getDataInicio(),
                compromisso.getDataFim(),
                compromisso.getStatus(),
                compromisso.getCreatedAt(),
                compromisso.getUpdatedAt());
    }

    public static Compromisso paraDominio(CompromissoJpaEntity entity) {
        return Compromisso.reconstituir(
                entity.getId(),
                entity.getGestorId(),
                entity.getEventoId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

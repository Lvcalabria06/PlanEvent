package infrastructure.persistence.agenda.mapper;

import domain.agenda.entity.Lembrete;
import infrastructure.persistence.agenda.entity.LembreteJpaEntity;

public final class LembreteMapper {

    private LembreteMapper() {
    }

    public static LembreteJpaEntity paraJpa(Lembrete lembrete) {
        return new LembreteJpaEntity(
                lembrete.getId(),
                lembrete.getCompromissoId(),
                lembrete.getEventoId(),
                lembrete.getHorario(),
                lembrete.isNotificado(),
                lembrete.getCreatedAt(),
                lembrete.getUpdatedAt());
    }

    public static Lembrete paraDominio(LembreteJpaEntity entity) {
        return Lembrete.reconstituir(
                entity.getId(),
                entity.getCompromissoId(),
                entity.getEventoId(),
                entity.getHorario(),
                entity.isNotificado(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

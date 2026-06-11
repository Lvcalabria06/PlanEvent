package application.agenda.mapper;

import application.agenda.dto.CompromissoResponse;
import application.agenda.dto.LembreteResponse;
import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;

public final class AgendaDtoMapper {

    private AgendaDtoMapper() {
    }

    public static CompromissoResponse paraResposta(Compromisso compromisso) {
        return new CompromissoResponse(
                compromisso.getId(),
                compromisso.getGestorId(),
                compromisso.getEventoId(),
                compromisso.getTitulo(),
                compromisso.getDescricao(),
                compromisso.getDataInicio(),
                compromisso.getDataFim(),
                compromisso.getStatus() != null ? compromisso.getStatus().name() : null,
                compromisso.getCreatedAt(),
                compromisso.getUpdatedAt());
    }

    public static LembreteResponse paraResposta(Lembrete lembrete) {
        return new LembreteResponse(
                lembrete.getId(),
                lembrete.getCompromissoId(),
                lembrete.getEventoId(),
                lembrete.getHorario(),
                lembrete.isNotificado(),
                lembrete.getCreatedAt(),
                lembrete.getUpdatedAt());
    }
}

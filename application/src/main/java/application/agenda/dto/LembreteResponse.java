package application.agenda.dto;

import java.time.LocalDateTime;

public record LembreteResponse(
        String id,
        String compromissoId,
        String eventoId,
        LocalDateTime horario,
        boolean notificado,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

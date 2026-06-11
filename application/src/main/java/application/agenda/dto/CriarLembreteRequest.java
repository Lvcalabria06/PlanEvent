package application.agenda.dto;

import java.time.LocalDateTime;

public record CriarLembreteRequest(
        String compromissoId,
        String eventoId,
        LocalDateTime horario) {
}

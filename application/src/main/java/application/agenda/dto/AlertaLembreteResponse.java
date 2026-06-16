package application.agenda.dto;

import java.time.LocalDateTime;

public record AlertaLembreteResponse(
        String lembreteId,
        String mensagem,
        LocalDateTime horario) {
}

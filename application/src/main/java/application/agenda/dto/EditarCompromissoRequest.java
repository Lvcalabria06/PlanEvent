package application.agenda.dto;

import java.time.LocalDateTime;

public record EditarCompromissoRequest(
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}

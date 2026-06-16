package application.agenda.dto;

import java.time.LocalDateTime;

public record CompromissoResponse(
        String id,
        String gestorId,
        String eventoId,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

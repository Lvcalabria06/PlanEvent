package application.agenda.dto;

import java.time.LocalDateTime;

public record CriarCompromissoRequest(
        String gestorId,
        String eventoId,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}

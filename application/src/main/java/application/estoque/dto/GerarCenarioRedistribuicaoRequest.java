package application.estoque.dto;

import java.time.LocalDateTime;

public record GerarCenarioRedistribuicaoRequest(
        String usuarioId,
        LocalDateTime periodoInicio,
        LocalDateTime periodoFim) {
}

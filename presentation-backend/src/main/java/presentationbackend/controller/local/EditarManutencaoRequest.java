package presentationbackend.controller.local;

import java.time.LocalDateTime;

public record EditarManutencaoRequest(
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String responsavel
) {}

package presentationbackend.controller.local;

import java.time.LocalDateTime;

public record CadastrarManutencaoRequest(
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String responsavel
) {}

package application.evento.dto;

import java.time.LocalDateTime;

public record TrocaLocalPlanejamentoDto(
        LocalDateTime dataHora,
        String usuarioId,
        String motivo,
        String localAnteriorId,
        String localAnteriorNome,
        String localNovoId,
        String localNovoNome
) {}

package application.conciliacao.dto;

public record VincularManualmenteRequest(
        String despesaId,
        String contratoId,
        String responsavelId
) {}

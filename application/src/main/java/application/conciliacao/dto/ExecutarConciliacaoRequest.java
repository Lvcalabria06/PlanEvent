package application.conciliacao.dto;

public record ExecutarConciliacaoRequest(
        String eventoId,
        String responsavelId
) {}

package application.evento.dto;

public record TrocaLocalContingenciaRequest(
        String novoLocalId,
        String usuarioId,
        String motivo
) {}

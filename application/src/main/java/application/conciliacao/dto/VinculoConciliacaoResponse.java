package application.conciliacao.dto;

import domain.conciliacao.valueobject.MetodoConciliacao;

import java.time.LocalDateTime;

public record VinculoConciliacaoResponse(
        String id,
        String eventoId,
        String despesaId,
        String contratoId,
        MetodoConciliacao metodo,
        String responsavelId,
        LocalDateTime dataConciliacao,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

package presentationbackend.controller.local;

import domain.local.turno.entity.TurnoOperacional;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record TurnoResponse(
        String id,
        String localId,
        String nome,
        LocalTime horaInicio,
        LocalTime horaFim,
        String diasDaSemana,
        String status,
        Integer capacidade,
        String observacoes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TurnoResponse de(TurnoOperacional turno) {
        return new TurnoResponse(
                turno.getId(),
                turno.getLocalId(),
                turno.getNome(),
                turno.getHoraInicio(),
                turno.getHoraFim(),
                turno.getDiasDaSemana(),
                turno.getStatus().name(),
                turno.getCapacidade(),
                turno.getObservacoes(),
                turno.getCreatedAt(),
                turno.getUpdatedAt()
        );
    }
}

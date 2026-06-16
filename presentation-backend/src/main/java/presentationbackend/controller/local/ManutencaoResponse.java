package presentationbackend.controller.local;

import domain.local.entity.ManutencaoLocal;

import java.time.LocalDateTime;

public record ManutencaoResponse(
        String id,
        String localId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String responsavel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ManutencaoResponse de(ManutencaoLocal manutencao) {
        return new ManutencaoResponse(
                manutencao.getId(),
                manutencao.getLocalId(),
                manutencao.getDataInicio(),
                manutencao.getDataFim(),
                manutencao.getResponsavel(),
                manutencao.getCreatedAt(),
                manutencao.getUpdatedAt()
        );
    }
}

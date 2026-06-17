package presentationbackend.controller.local;

import domain.local.entity.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LocalResponse(
        String id,
        String nome,
        int capacidade,
        String endereco,
        String tipo,
        String infraestrutura,
        String restricoes,
        BigDecimal custo,
        String status,
        LocalDateTime updatedAt
) {
    public static LocalResponse de(Local local) {
        return new LocalResponse(
                local.getId(),
                local.getNome(),
                local.getCapacidade(),
                local.getEndereco(),
                local.getTipo(),
                local.getInfraestrutura(),
                local.getRestricoes(),
                local.getCusto(),
                local.getStatus().name(),
                local.getUpdatedAt()
        );
    }
}

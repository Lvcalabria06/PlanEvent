package application.equipe.dto;

import java.time.LocalDateTime;

/**
 * Representação de saída de um membro de equipe.
 */
public record MembroEquipeResponse(
        String id,
        String funcionarioId,
        boolean lider,
        LocalDateTime dataEntrada) {
}

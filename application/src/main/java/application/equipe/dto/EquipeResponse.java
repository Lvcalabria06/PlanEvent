package application.equipe.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representação de saída de uma equipe, isolando a camada web da entidade de domínio.
 */
public record EquipeResponse(
        String id,
        String eventoId,
        String nome,
        List<MembroEquipeResponse> membros,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao) {
}

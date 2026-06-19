package application.funcionario.dto;

import java.time.LocalDateTime;

/**
 * Representação de saída de um funcionário, isolando a camada web da entidade de domínio.
 */
public record FuncionarioResponse(
        String id,
        String nome,
        String cargo,
        String disponibilidade,
        boolean ativo,
        java.util.List<String> competencias,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

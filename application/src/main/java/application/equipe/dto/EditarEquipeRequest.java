package application.equipe.dto;

import java.util.List;

/**
 * Dados de entrada para editar uma equipe existente.
 */
public record EditarEquipeRequest(
        String nome,
        List<MembroEquipeRequest> membros) {
}

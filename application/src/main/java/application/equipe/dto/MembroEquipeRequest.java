package application.equipe.dto;

/**
 * Representa um membro ao criar ou editar uma equipe.
 */
public record MembroEquipeRequest(
        String funcionarioId,
        boolean lider) {
}

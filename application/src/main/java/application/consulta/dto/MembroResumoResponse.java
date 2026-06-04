package application.consulta.dto;

/**
 * Membro de uma equipe, com o nome do funcionário já resolvido.
 */
public record MembroResumoResponse(String funcionarioId, String nome, boolean lider) {
}

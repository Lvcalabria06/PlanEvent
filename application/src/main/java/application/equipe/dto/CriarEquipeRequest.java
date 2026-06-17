package application.equipe.dto;

import java.util.List;

/**
 * Dados de entrada para cadastrar uma nova equipe.
 */
public record CriarEquipeRequest(
        String eventoId,
        String nome,
        List<MembroEquipeRequest> membros) {
}

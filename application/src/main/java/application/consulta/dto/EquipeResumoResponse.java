package application.consulta.dto;

import java.util.List;

/**
 * Resumo de equipe para a camada de apresentação (seleção de equipe ao criar
 * tarefas), com seus membros já resolvidos.
 */
public record EquipeResumoResponse(
        String id,
        String nome,
        String eventoId,
        List<MembroResumoResponse> membros) {
}

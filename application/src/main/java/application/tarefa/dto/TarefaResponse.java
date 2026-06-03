package application.tarefa.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Representação de saída de uma tarefa, isolando a camada web da entidade de
 * domínio. Inclui status, dependências (predecessoras) e responsáveis (CA17).
 */
public record TarefaResponse(
        String id,
        String equipeId,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String status,
        List<String> dependencias,
        List<String> responsaveis,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao) {
}

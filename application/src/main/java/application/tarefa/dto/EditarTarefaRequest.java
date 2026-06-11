package application.tarefa.dto;

import java.time.LocalDateTime;

/**
 * Dados de entrada para editar uma tarefa existente (Passo 1). O id é informado
 * separadamente (ex.: pela rota), por isso não consta aqui.
 */
public record EditarTarefaRequest(
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}

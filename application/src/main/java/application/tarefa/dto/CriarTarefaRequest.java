package application.tarefa.dto;

import java.time.LocalDateTime;

/**
 * Dados de entrada para cadastrar uma nova tarefa (Passo 1).
 */
public record CriarTarefaRequest(
        String equipeId,
        String titulo,
        String descricao,
        LocalDateTime dataInicio,
        LocalDateTime dataFim) {
}

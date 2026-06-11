package application.consulta.dto;

/**
 * Resumo de funcionário para alimentar selects da camada de apresentação
 * (ex.: responsáveis de uma tarefa).
 */
public record FuncionarioResumoResponse(String id, String nome, String cargo) {
}

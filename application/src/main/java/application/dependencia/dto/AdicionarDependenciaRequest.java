package application.dependencia.dto;

/**
 * Dados de entrada para definir que uma tarefa depende de outra (Passo 2). O id
 * da tarefa dependente é informado pela rota; aqui vai a predecessora.
 */
public record AdicionarDependenciaRequest(String tarefaPredecessoraId) {
}

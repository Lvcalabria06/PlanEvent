package application.dependencia.usecase;

import application.dependencia.dto.AdicionarDependenciaRequest;
import application.tarefa.dto.TarefaResponse;

import java.util.List;

/**
 * Casos de uso de gerenciamento de dependências entre tarefas
 * (Funcionalidade 5 - Passo 2).
 */
public interface DependenciaUseCase {

    void adicionar(String tarefaId, AdicionarDependenciaRequest request);

    void remover(String tarefaId, String tarefaPredecessoraId);

    /** Tarefas das quais a tarefa informada depende (predecessoras). CA13. */
    List<TarefaResponse> listarDependencias(String tarefaId);

    /** Tarefas que dependem da tarefa informada (dependentes). CA14. */
    List<TarefaResponse> listarDependentes(String tarefaId);
}

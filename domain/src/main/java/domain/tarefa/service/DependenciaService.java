package domain.tarefa.service;

import domain.tarefa.entity.Tarefa;
import java.util.List;

public interface DependenciaService {

    /**
     * Define que a tarefa (tarefaId) depende da conclusão da tarefa predecessora.
     */
    void adicionarDependencia(String tarefaId, String tarefaPredecessoraId);

    /**
     * Remove uma dependência específica de uma tarefa.
     */
    void removerDependencia(String tarefaId, String tarefaPredecessoraId);

    /**
     * Retorna todas as tarefas (A, B, C...) das quais a tarefa dependente atual precisa aguardar.
     */
    List<Tarefa> listarDependencias(String tarefaId);

    /**
     * Retorna todas as tarefas que dependem da (ou seja, estão aguardando) tarefa informada.
     */
    List<Tarefa> listarDependentes(String tarefaId);

    /**
     * Analisa se a nova dataFim da tarefa impacta (é maior que a dataInicio) alguma das suas dependentes.
     * Retorna true se houver dependentes impactadas.
     */
    boolean possuiImpactoAtraso(String tarefaPredecessoraId);
}

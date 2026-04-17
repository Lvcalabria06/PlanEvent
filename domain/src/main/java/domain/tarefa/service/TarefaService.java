package domain.tarefa.service;

import java.util.List;

import domain.tarefa.entity.Tarefa;

public interface TarefaService {

    Tarefa criarTarefa(Tarefa tarefa);

    Tarefa editarTarefa(Tarefa tarefa);

    void removerTarefa(String tarefaId);

    void iniciarTarefa(String tarefaId);

    void atribuirResponsavel(String tarefaId, String funcionarioId);

    List<Tarefa> listarPorEquipe(String equipeId);
}

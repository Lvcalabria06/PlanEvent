package domain.tarefa.service;

import java.time.LocalDateTime;
import java.util.List;

import domain.tarefa.entity.Tarefa;

public interface TarefaService {

    Tarefa criarTarefa(Tarefa tarefa);

    Tarefa editarTarefa(Tarefa tarefa);

    Tarefa editarTarefa(String tarefaId, String titulo, String descricao,
            LocalDateTime dataInicio, LocalDateTime dataFim);

    void removerTarefa(String tarefaId);

    void iniciarTarefa(String tarefaId);

    void concluirTarefa(String tarefaId);

    void atribuirResponsavel(String tarefaId, String funcionarioId);

    List<String> listarResponsaveis(String tarefaId);

    List<Tarefa> listarPorEquipe(String equipeId);

    List<Tarefa> listarPorEvento(String eventoId);
}

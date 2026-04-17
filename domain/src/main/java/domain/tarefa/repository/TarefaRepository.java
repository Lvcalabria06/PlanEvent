package domain.tarefa.repository;

import java.util.List;
import java.util.Optional;

import domain.tarefa.entity.Tarefa;

public interface TarefaRepository {

    Tarefa salvar(Tarefa tarefa);
    Optional<Tarefa> buscarPorId(String id);
    void remover(String id);
    List<Tarefa> listarPorEquipeId(String equipeId);
    List<Tarefa> listarPorEventoId(String eventoId);
    boolean existePorTituloEEquipe(String titulo, String equipeId);
    List<Tarefa> listarPorIds(List<String> ids);
    List<Tarefa> listarDependentes(String tarefaId);
}

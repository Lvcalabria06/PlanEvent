package domain.tarefa.repository;

import domain.tarefa.entity.ResponsavelTarefa;
import java.util.List;

public interface ResponsavelTarefaRepository {
    void salvar(ResponsavelTarefa responsavel);
    void remover(String id);
    List<ResponsavelTarefa> listarPorTarefa(String tarefaId);
    boolean existePorTarefaEFuncionario(String tarefaId, String funcionarioId);
}

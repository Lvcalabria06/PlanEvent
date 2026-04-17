package domain.tarefa.valueobject;

import java.util.Objects;

public class DependenciaTarefa {
    private final String tarefaPredecessoraId;

    public DependenciaTarefa(String tarefaPredecessoraId) {
        if (tarefaPredecessoraId == null || tarefaPredecessoraId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da tarefa predecessora não pode ser nulo ou vazio.");
        }
        this.tarefaPredecessoraId = tarefaPredecessoraId;
    }

    public String getTarefaPredecessoraId() {
        return tarefaPredecessoraId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependenciaTarefa that = (DependenciaTarefa) o;
        return Objects.equals(tarefaPredecessoraId, that.tarefaPredecessoraId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tarefaPredecessoraId);
    }
}

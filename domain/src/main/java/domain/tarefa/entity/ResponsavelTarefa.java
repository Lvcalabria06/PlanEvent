package domain.tarefa.entity;

import java.util.UUID;

public class ResponsavelTarefa {
    private final String id;
    private final String tarefaId;
    private final String funcionarioId;

    public ResponsavelTarefa() {
        this.id = UUID.randomUUID().toString();
        this.tarefaId = null;
        this.funcionarioId = null;
    }

    public ResponsavelTarefa(String tarefaId, String funcionarioId) {
        if (tarefaId == null || funcionarioId == null) {
            throw new IllegalArgumentException("IDs de tarefa e funcionário são obrigatórios.");
        }
        this.id = UUID.randomUUID().toString();
        this.tarefaId = tarefaId;
        this.funcionarioId = funcionarioId;
    }

    // Getters
    public String getId() { return id; }
    public String getTarefaId() { return tarefaId; }
    public String getFuncionarioId() { return funcionarioId; }
}

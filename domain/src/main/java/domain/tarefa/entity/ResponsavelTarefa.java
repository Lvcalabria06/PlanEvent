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

    private ResponsavelTarefa(String id, String tarefaId, String funcionarioId) {
        this.id = id;
        this.tarefaId = tarefaId;
        this.funcionarioId = funcionarioId;
    }

    /**
     * Reconstrói o vínculo a partir de dados persistidos, preservando o id.
     * Usado pela camada de persistência (mapeamento objeto-relacional).
     */
    public static ResponsavelTarefa reconstituir(String id, String tarefaId, String funcionarioId) {
        return new ResponsavelTarefa(id, tarefaId, funcionarioId);
    }

    // Getters
    public String getId() { return id; }
    public String getTarefaId() { return tarefaId; }
    public String getFuncionarioId() { return funcionarioId; }
}

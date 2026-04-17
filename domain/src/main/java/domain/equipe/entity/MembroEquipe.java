package domain.equipe.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MembroEquipe {
    private final String id;
    private final String equipeId;
    private final String funcionarioId;
    private final LocalDateTime dataEntrada;

    public MembroEquipe() {
        this.id = UUID.randomUUID().toString();
        this.equipeId = null;
        this.funcionarioId = null;
        this.dataEntrada = LocalDateTime.now();
    }

    public MembroEquipe(String equipeId, String funcionarioId) {
        if (equipeId == null || funcionarioId == null) {
            throw new IllegalArgumentException("IDs de equipe e funcionário são obrigatórios.");
        }
        this.id = UUID.randomUUID().toString();
        this.equipeId = equipeId;
        this.funcionarioId = funcionarioId;
        this.dataEntrada = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getEquipeId() { return equipeId; }
    public String getFuncionarioId() { return funcionarioId; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
}

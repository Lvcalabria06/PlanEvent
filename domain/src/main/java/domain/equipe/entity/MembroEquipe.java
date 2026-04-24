package domain.equipe.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class MembroEquipe {
    private final String id;
    private final String funcionarioId;
    private boolean lider;
    private final LocalDateTime dataEntrada;

    public MembroEquipe(String funcionarioId, boolean lider) {
        if (funcionarioId == null || funcionarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("Funcionário é obrigatório.");
        }

        this.id = UUID.randomUUID().toString();
        this.funcionarioId = funcionarioId;
        this.lider = lider;
        this.dataEntrada = LocalDateTime.now();
    }

    public void definirComoLider() {
        this.lider = true;
    }

    public void removerLideranca() {
        this.lider = false;
    }

    public String getId() { return id; }
    public String getFuncionarioId() { return funcionarioId; }
    public boolean isLider() { return lider; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
}
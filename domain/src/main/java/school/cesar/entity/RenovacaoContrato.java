package school.cesar.entity;

import java.time.LocalDateTime;

public class RenovacaoContrato {
    private String id;
    private String contratoId; // FK
    private LocalDateTime novaDataFim;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RenovacaoContrato() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContratoId() { return contratoId; }
    public void setContratoId(String contratoId) { this.contratoId = contratoId; }

    public LocalDateTime getNovaDataFim() { return novaDataFim; }
    public void setNovaDataFim(LocalDateTime novaDataFim) { this.novaDataFim = novaDataFim; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

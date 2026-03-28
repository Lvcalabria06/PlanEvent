package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class AvaliacaoLocal {
    private String id;
    private String eventoId; // FK
    private String localId; // FK
    private NivelAdequacao nivelAdequacao;
    private String justificativa; // JSON ou string
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AvaliacaoLocal() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public NivelAdequacao getNivelAdequacao() { return nivelAdequacao; }
    public void setNivelAdequacao(NivelAdequacao nivelAdequacao) { this.nivelAdequacao = nivelAdequacao; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

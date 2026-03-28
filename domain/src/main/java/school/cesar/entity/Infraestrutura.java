package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class Infraestrutura {
    private String id;
    private String localId; // FK
    private TipoInfraestrutura tipo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Infraestrutura() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public TipoInfraestrutura getTipo() { return tipo; }
    public void setTipo(TipoInfraestrutura tipo) { this.tipo = tipo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

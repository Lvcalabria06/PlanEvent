package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class DisponibilidadeLocal {
    private String id;
    private String localId; // FK
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusDisponibilidadeLocal tipo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DisponibilidadeLocal() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLocalId() { return localId; }
    public void setLocalId(String localId) { this.localId = localId; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusDisponibilidadeLocal getTipo() { return tipo; }
    public void setTipo(StatusDisponibilidadeLocal tipo) { this.tipo = tipo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

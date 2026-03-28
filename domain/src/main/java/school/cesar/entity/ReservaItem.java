package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class ReservaItem {
    private String id;
    private String itemId; // FK
    private String eventoId; // FK
    private Integer quantidade;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReservaItem status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservaItem() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusReservaItem getStatus() { return status; }
    public void setStatus(StatusReservaItem status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

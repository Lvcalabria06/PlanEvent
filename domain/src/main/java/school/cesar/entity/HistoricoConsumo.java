package school.cesar.entity;

import java.time.LocalDateTime;

public class HistoricoConsumo {
    private String id;
    private String eventoId; // FK
    private String itemId; // FK
    private Integer quantidadeConsumida;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public HistoricoConsumo() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public Integer getQuantidadeConsumida() { return quantidadeConsumida; }
    public void setQuantidadeConsumida(Integer quantidadeConsumida) { this.quantidadeConsumida = quantidadeConsumida; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

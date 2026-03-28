package school.cesar.entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Orcamento {
    private String id;
    private String eventoId; // FK
    private String categoriaId; // FK
    private BigDecimal valorPrevisto;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Orcamento() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getCategoriaId() { return categoriaId; }
    public void setCategoriaId(String categoriaId) { this.categoriaId = categoriaId; }

    public BigDecimal getValorPrevisto() { return valorPrevisto; }
    public void setValorPrevisto(BigDecimal valorPrevisto) { this.valorPrevisto = valorPrevisto; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

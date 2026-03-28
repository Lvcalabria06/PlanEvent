package school.cesar.entity;

import java.time.LocalDateTime;

public class CategoriaFinanceira {
    private String id;
    private String nome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoriaFinanceira() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package infrastructure.persistence.local.entity;

import domain.local.valueobject.StatusLocal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "local")
public class LocalJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "capacidade", nullable = false)
    private int capacidade;

    @Column(name = "endereco", nullable = false)
    private String endereco;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "infraestrutura", nullable = false, columnDefinition = "TEXT")
    private String infraestrutura;

    @Column(name = "restricoes", columnDefinition = "TEXT")
    private String restricoes;

    @Column(name = "custo", precision = 15, scale = 2)
    private BigDecimal custo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusLocal status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected LocalJpaEntity() {}

    public LocalJpaEntity(String id, String nome, int capacidade, String endereco, String tipo,
                          String infraestrutura, String restricoes, BigDecimal custo,
                          StatusLocal status, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.endereco = endereco;
        this.tipo = tipo;
        this.infraestrutura = infraestrutura;
        this.restricoes = restricoes;
        this.custo = custo;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public String getEndereco() { return endereco; }
    public String getTipo() { return tipo; }
    public String getInfraestrutura() { return infraestrutura; }
    public String getRestricoes() { return restricoes; }
    public BigDecimal getCusto() { return custo; }
    public StatusLocal getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

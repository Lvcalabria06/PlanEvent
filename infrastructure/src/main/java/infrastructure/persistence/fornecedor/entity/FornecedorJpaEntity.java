package infrastructure.persistence.fornecedor.entity;

import domain.fornecedor.valueobject.StatusFornecedor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "fornecedor")
public class FornecedorJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cnpj", nullable = false, unique = true)
    private String cnpj;

    @Column(name = "categoria_servico", nullable = false)
    private String categoriaServico;

    @Column(name = "contato", nullable = false)
    private String contato;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusFornecedor status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected FornecedorJpaEntity() {}

    public FornecedorJpaEntity(String id, String nome, String cnpj, String categoriaServico,
                               String contato, StatusFornecedor status,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.categoriaServico = categoriaServico;
        this.contato = contato;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getCategoriaServico() { return categoriaServico; }
    public String getContato() { return contato; }
    public StatusFornecedor getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

package infrastructure.persistence.funcionario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Mapeamento objeto-relacional de Funcionário.
 */
@Entity
@Table(name = "funcionario")
public class FuncionarioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cargo", nullable = false)
    private String cargo;

    @Column(name = "disponibilidade", nullable = false)
    private String disponibilidade;

    @Column(name = "competencias")
    private String competencias;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected FuncionarioJpaEntity() {
    }

    public FuncionarioJpaEntity(String id, String nome, String cargo, String disponibilidade,
            boolean ativo, String competencias, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.disponibilidade = disponibilidade;
        this.ativo = ativo;
        this.competencias = competencias;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public String getDisponibilidade() { return disponibilidade; }
    public boolean isAtivo() { return ativo; }
    public String getCompetencias() { return competencias; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

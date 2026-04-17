package domain.funcionario.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Funcionario {
    private final String id;
    private String nome;
    private String cargo;
    private boolean disponibilidade;
    private boolean ativo;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Funcionario() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Funcionario(String nome, String cargo, boolean disponibilidade) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.cargo = cargo;
        this.disponibilidade = disponibilidade;
        this.ativo = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void ativar() {
        this.ativo = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void inativar() {
        this.ativo = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void alterarDisponibilidade(boolean disponivel) {
        this.disponibilidade = disponivel;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public boolean isDisponibilidade() { return disponibilidade; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

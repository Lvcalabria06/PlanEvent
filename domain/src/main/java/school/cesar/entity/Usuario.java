package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;
    private RoleUsuario role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Usuario() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public RoleUsuario getRole() { return role; }
    public void setRole(RoleUsuario role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

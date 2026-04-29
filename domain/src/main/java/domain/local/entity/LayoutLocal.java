package domain.local.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class LayoutLocal {
    private final String id;
    private String nome;
    private String descricao;
    private int capacidadeMaxima;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String usuarioCriacao;
    private String usuarioAtualizacao;

    public LayoutLocal(String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel) {
        validar(nome, capacidadeMaxima, usuarioResponsavel);
        this.id = UUID.randomUUID().toString();
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : "";
        this.capacidadeMaxima = capacidadeMaxima;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
        this.usuarioCriacao = usuarioResponsavel.trim();
        this.usuarioAtualizacao = usuarioResponsavel.trim();
    }

    public void atualizar(String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel) {
        validar(nome, capacidadeMaxima, usuarioResponsavel);
        this.nome = nome.trim();
        this.descricao = descricao != null ? descricao.trim() : "";
        this.capacidadeMaxima = capacidadeMaxima;
        this.dataAtualizacao = LocalDateTime.now();
        this.usuarioAtualizacao = usuarioResponsavel.trim();
    }

    private void validar(String nome, int capacidadeMaxima, String usuarioResponsavel) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do layout é obrigatório.");
        }
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade máxima do layout deve ser maior que zero.");
        }
        if (usuarioResponsavel == null || usuarioResponsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public String getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public String getUsuarioAtualizacao() {
        return usuarioAtualizacao;
    }
}

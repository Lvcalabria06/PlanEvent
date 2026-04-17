package domain.equipe.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Equipe {
    private final String id;
    private final String eventoId;
    private String nome;
    private String liderId;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Equipe() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public Equipe(String eventoId, String nome, String liderId) {
        if (eventoId == null || nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("EventoID e Nome são obrigatórios.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.nome = nome;
        this.liderId = liderId;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void alterarLider(String novoLiderId) {
        this.liderId = novoLiderId;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void alterarNome(String novoNome) {
        if (novoNome == null || novoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = novoNome;
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getNome() { return nome; }
    public String getLiderId() { return liderId; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}

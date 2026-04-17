package domain.agenda.entity;

import domain.agenda.valueobject.StatusCompromisso;

import java.time.LocalDateTime;
import java.util.UUID;

public class Compromisso {
    private final String id;
    private final String gestorId;
    private String titulo;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusCompromisso status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Compromisso() {
        this.id = UUID.randomUUID().toString();
        this.gestorId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Compromisso(String gestorId, String titulo, String descricao, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (gestorId == null) {
            throw new IllegalArgumentException("ID do gestor é obrigatório.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas inválidas.");
        }
        this.id = UUID.randomUUID().toString();
        this.gestorId = gestorId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusCompromisso.AGENDADO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void remarcar(LocalDateTime novaDataInicio, LocalDateTime novaDataFim) {
        if (novaDataInicio == null || novaDataFim == null || novaDataInicio.isAfter(novaDataFim)) {
            throw new IllegalArgumentException("Datas inválidas para remarcação.");
        }
        if (this.status == StatusCompromisso.CANCELADO || this.status == StatusCompromisso.REALIZADO) {
            throw new IllegalStateException("Não é possível remarcar um compromisso cancelado ou já realizado.");
        }
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
        this.status = StatusCompromisso.ADIADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusCompromisso.REALIZADO) {
            throw new IllegalStateException("Um compromisso realizado não pode ser cancelado.");
        }
        this.status = StatusCompromisso.CANCELADO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void concluir() {
        if (this.status != StatusCompromisso.AGENDADO && this.status != StatusCompromisso.ADIADO) {
            throw new IllegalStateException("Apenas compromissos agendados/adiados podem ser concluídos.");
        }
        this.status = StatusCompromisso.REALIZADO;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getGestorId() { return gestorId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusCompromisso getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

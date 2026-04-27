package domain.agenda.entity;

import domain.agenda.valueobject.StatusCompromisso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Compromisso {
    private final String id;
    private final String gestorId;
    private String titulo;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusCompromisso status;
    private final List<Lembrete> lembretes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Compromisso(String gestorId, String titulo, String descricao,
                       LocalDateTime dataInicio, LocalDateTime dataFim) {

        validarCamposObrigatorios(gestorId, titulo, dataInicio, dataFim);

        if (dataInicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é permitido criar compromissos em datas passadas.");
        }

        this.id = UUID.randomUUID().toString();
        this.gestorId = gestorId;
        this.titulo = titulo.trim();
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusCompromisso.AGENDADO;
        this.lembretes = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void editar(String titulo, String descricao,
                       LocalDateTime dataInicio, LocalDateTime dataFim) {

        if (this.status == StatusCompromisso.REALIZADO) {
            throw new IllegalStateException("Não é permitido editar compromissos já concluídos.");
        }
        if (this.status == StatusCompromisso.CANCELADO) {
            throw new IllegalStateException("Não é permitido editar compromissos cancelados.");
        }

        validarCamposObrigatorios(this.gestorId, titulo, dataInicio, dataFim);

        this.titulo = titulo.trim();
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.updatedAt = LocalDateTime.now();
    }

    public void validarExclusao() {
        if (this.status == StatusCompromisso.EM_ANDAMENTO) {
            throw new IllegalStateException("Compromissos em andamento não podem ser excluídos.");
        }
    }

    public void iniciar() {
        if (this.status != StatusCompromisso.AGENDADO && this.status != StatusCompromisso.ADIADO) {
            throw new IllegalStateException("Apenas compromissos agendados ou adiados podem ser iniciados.");
        }
        this.status = StatusCompromisso.EM_ANDAMENTO;
        this.updatedAt = LocalDateTime.now();
    }

    public void concluir() {
        if (this.status != StatusCompromisso.AGENDADO && this.status != StatusCompromisso.ADIADO
                && this.status != StatusCompromisso.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas compromissos agendados, adiados ou em andamento podem ser concluídos.");
        }
        this.status = StatusCompromisso.REALIZADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusCompromisso.REALIZADO) {
            throw new IllegalStateException("Um compromisso realizado não pode ser cancelado.");
        }
        this.status = StatusCompromisso.CANCELADO;
        this.updatedAt = LocalDateTime.now();
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

    public boolean temSobreposicao(Compromisso outro) {
        if (!this.gestorId.equals(outro.gestorId)) {
            return false;
        }
        if (this.id.equals(outro.id)) {
            return false;
        }
        return this.dataInicio.isBefore(outro.dataFim) && outro.dataInicio.isBefore(this.dataFim);
    }

    public boolean estaFinalizado() {
        return this.status == StatusCompromisso.REALIZADO || this.status == StatusCompromisso.CANCELADO;
    }

    private static void validarCamposObrigatorios(String gestorId, String titulo,
                                                   LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (gestorId == null || gestorId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do gestor é obrigatório.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de início e fim são obrigatórias.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Horário de fim não pode ser anterior ao início.");
        }
    }

    public String getId() { return id; }
    public String getGestorId() { return gestorId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusCompromisso getStatus() { return status; }
    public List<Lembrete> getLembretes() { return Collections.unmodifiableList(lembretes); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

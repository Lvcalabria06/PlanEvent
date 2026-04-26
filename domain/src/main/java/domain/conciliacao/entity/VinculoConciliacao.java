package domain.conciliacao.entity;

import domain.conciliacao.valueobject.MetodoConciliacao;

import java.time.LocalDateTime;
import java.util.UUID;

public class VinculoConciliacao {

    private final String id;
    private final String eventoId;
    private final String despesaId;

    private String contratoId;
    private MetodoConciliacao metodo;
    private String responsavelId;
    private LocalDateTime dataConciliacao;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VinculoConciliacao(String eventoId, String despesaId, String contratoId,
            MetodoConciliacao metodo, String responsavelId) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (despesaId == null || despesaId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da despesa é obrigatório.");
        }
        if (contratoId == null || contratoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do contrato é obrigatório.");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Método de conciliação é obrigatório.");
        }
        if (responsavelId == null || responsavelId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do responsável é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.despesaId = despesaId;
        this.contratoId = contratoId;
        this.metodo = metodo;
        this.responsavelId = responsavelId;
        this.dataConciliacao = LocalDateTime.now();
        this.createdAt = this.dataConciliacao;
        this.updatedAt = this.createdAt;
    }

    public void substituirVinculo(String novoContratoId, String responsavelId, MetodoConciliacao metodo) {
        if (novoContratoId == null || novoContratoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do novo contrato é obrigatório.");
        }
        if (responsavelId == null || responsavelId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do responsável é obrigatório.");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("Método de conciliação é obrigatório.");
        }
        this.contratoId = novoContratoId;
        this.responsavelId = responsavelId;
        this.metodo = metodo;
        this.dataConciliacao = LocalDateTime.now();
        this.updatedAt = this.dataConciliacao;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getDespesaId() { return despesaId; }
    public String getContratoId() { return contratoId; }
    public MetodoConciliacao getMetodo() { return metodo; }
    public String getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataConciliacao() { return dataConciliacao; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

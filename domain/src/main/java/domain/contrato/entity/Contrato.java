package domain.contrato.entity;

import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Contrato {
    private final String id;
    private TipoContrato tipo;
    private String objeto;
    private BigDecimal valor;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusContrato status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Contrato() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Contrato(TipoContrato tipo, String objeto, BigDecimal valor, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de contrato é obrigatório.");
        }
        if (objeto == null || objeto.trim().isEmpty()) {
            throw new IllegalArgumentException("Objeto do contrato é obrigatório.");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser depois da data de fim.");
        }
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.objeto = objeto;
        this.valor = valor != null ? valor : BigDecimal.ZERO;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusContrato.RASCUNHO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void assinar() {
        if (this.status != StatusContrato.RASCUNHO && this.status != StatusContrato.EM_NEGOCIACAO) {
            throw new IllegalStateException("Contrato não está em um estado válido para assinatura.");
        }
        this.status = StatusContrato.ASSINADO;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancelar() {
        if (this.status == StatusContrato.ENCERRADO) {
            throw new IllegalStateException("Um contrato encerrado não pode ser cancelado.");
        }
        this.status = StatusContrato.CANCELADO;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public TipoContrato getTipo() { return tipo; }
    public String getObjeto() { return objeto; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusContrato getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

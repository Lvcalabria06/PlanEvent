package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Contrato {
    private String id;
    private String eventoId; // FK
    private String tipo;
    private String partes;
    private String objeto;
    private BigDecimal valor;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusContrato status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Contrato() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPartes() { return partes; }
    public void setPartes(String partes) { this.partes = partes; }

    public String getObjeto() { return objeto; }
    public void setObjeto(String objeto) { this.objeto = objeto; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusContrato getStatus() { return status; }
    public void setStatus(StatusContrato status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

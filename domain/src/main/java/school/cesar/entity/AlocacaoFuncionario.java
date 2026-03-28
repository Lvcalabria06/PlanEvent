package school.cesar.entity;

import java.time.LocalDateTime;

public class AlocacaoFuncionario {
    private String id;
    private String funcionarioId; // FK
    private String eventoId; // FK
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlocacaoFuncionario() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(String funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

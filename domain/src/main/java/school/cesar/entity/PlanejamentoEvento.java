package school.cesar.entity;

import java.time.LocalDateTime;

public class PlanejamentoEvento {
    private String id;
    private String eventoId; // FK
    private String cronogramaGerado; // JSON text
    private String sugestaoEquipe;
    private String sugestaoRecursos;
    private String requisitosMinimos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlanejamentoEvento() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getCronogramaGerado() { return cronogramaGerado; }
    public void setCronogramaGerado(String cronogramaGerado) { this.cronogramaGerado = cronogramaGerado; }

    public String getSugestaoEquipe() { return sugestaoEquipe; }
    public void setSugestaoEquipe(String sugestaoEquipe) { this.sugestaoEquipe = sugestaoEquipe; }

    public String getSugestaoRecursos() { return sugestaoRecursos; }
    public void setSugestaoRecursos(String sugestaoRecursos) { this.sugestaoRecursos = sugestaoRecursos; }

    public String getRequisitosMinimos() { return requisitosMinimos; }
    public void setRequisitosMinimos(String requisitosMinimos) { this.requisitosMinimos = requisitosMinimos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

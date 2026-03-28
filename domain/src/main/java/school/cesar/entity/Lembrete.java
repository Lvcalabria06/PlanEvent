package school.cesar.entity;

import java.time.LocalDateTime;

public class Lembrete {
    private String id;
    private String eventoCalendarioId; // FK
    private LocalDateTime dataEnvio;
    private String mensagem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lembrete() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoCalendarioId() { return eventoCalendarioId; }
    public void setEventoCalendarioId(String eventoCalendarioId) { this.eventoCalendarioId = eventoCalendarioId; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

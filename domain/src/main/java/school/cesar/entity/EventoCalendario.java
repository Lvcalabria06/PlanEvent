package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class EventoCalendario {
    private String id;
    private String eventoId; // FK
    private String titulo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoEventoCalendario tipo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EventoCalendario() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public TipoEventoCalendario getTipo() { return tipo; }
    public void setTipo(TipoEventoCalendario tipo) { this.tipo = tipo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

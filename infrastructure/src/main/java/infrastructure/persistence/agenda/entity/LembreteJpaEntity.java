package infrastructure.persistence.agenda.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "lembrete")
public class LembreteJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "compromisso_id")
    private String compromissoId;

    @Column(name = "evento_id")
    private String eventoId;

    @Column(name = "horario", nullable = false)
    private LocalDateTime horario;

    @Column(name = "notificado", nullable = false)
    private boolean notificado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected LembreteJpaEntity() {
    }

    public LembreteJpaEntity(String id, String compromissoId, String eventoId, LocalDateTime horario,
                             boolean notificado, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.compromissoId = compromissoId;
        this.eventoId = eventoId;
        this.horario = horario;
        this.notificado = notificado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCompromissoId() {
        return compromissoId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public boolean isNotificado() {
        return notificado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

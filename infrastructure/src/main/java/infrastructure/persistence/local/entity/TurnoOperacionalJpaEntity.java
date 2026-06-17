package infrastructure.persistence.local.entity;

import domain.local.turno.valueobject.StatusTurno;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turno_operacional")
public class TurnoOperacionalJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "local_id", nullable = false, updatable = false)
    private String localId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name = "dias_da_semana", nullable = false)
    private String diasDaSemana;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusTurno status;

    @Column(name = "capacidade")
    private Integer capacidade;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TurnoOperacionalJpaEntity() {}

    public TurnoOperacionalJpaEntity(String id, String localId, String nome, LocalTime horaInicio, LocalTime horaFim,
                                     String diasDaSemana, StatusTurno status, Integer capacidade, String observacoes,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.localId = localId;
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diasDaSemana = diasDaSemana;
        this.status = status;
        this.capacidade = capacidade;
        this.observacoes = observacoes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getLocalId() { return localId; }
    public String getNome() { return nome; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public String getDiasDaSemana() { return diasDaSemana; }
    public StatusTurno getStatus() { return status; }
    public Integer getCapacidade() { return capacidade; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

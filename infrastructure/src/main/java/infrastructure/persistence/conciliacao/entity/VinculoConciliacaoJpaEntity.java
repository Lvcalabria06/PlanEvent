package infrastructure.persistence.conciliacao.entity;

import domain.conciliacao.valueobject.MetodoConciliacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "vinculo_conciliacao")
public class VinculoConciliacaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "despesa_id", nullable = false)
    private String despesaId;

    @Column(name = "contrato_id", nullable = false)
    private String contratoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false)
    private MetodoConciliacao metodo;

    @Column(name = "responsavel_id", nullable = false)
    private String responsavelId;

    @Column(name = "data_conciliacao", nullable = false)
    private LocalDateTime dataConciliacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected VinculoConciliacaoJpaEntity() {}

    public VinculoConciliacaoJpaEntity(String id, String eventoId, String despesaId, String contratoId,
            MetodoConciliacao metodo, String responsavelId, LocalDateTime dataConciliacao,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.eventoId = eventoId;
        this.despesaId = despesaId;
        this.contratoId = contratoId;
        this.metodo = metodo;
        this.responsavelId = responsavelId;
        this.dataConciliacao = dataConciliacao;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

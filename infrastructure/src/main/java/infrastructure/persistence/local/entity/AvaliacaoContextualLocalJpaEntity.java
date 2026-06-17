package infrastructure.persistence.local.entity;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao_contextual_local")
public class AvaliacaoContextualLocalJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false, updatable = false)
    private String eventoId;

    @Column(name = "local_id", nullable = false, updatable = false)
    private String localId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "porte_evento", nullable = false)
    private PorteEvento porteEvento;

    @Column(name = "participantes_contexto", nullable = false)
    private int participantesContexto;

    @Column(name = "notas_por_criterio", nullable = false, columnDefinition = "TEXT")
    private String notasPorCriterio;

    @Column(name = "nota_final", nullable = false)
    private double notaFinal;

    @Column(name = "justificativa", nullable = false, columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "usuario_responsavel", nullable = false)
    private String usuarioResponsavel;

    @Column(name = "data_hora_registro", nullable = false, updatable = false)
    private LocalDateTime dataHoraRegistro;

    protected AvaliacaoContextualLocalJpaEntity() {}

    public AvaliacaoContextualLocalJpaEntity(String id, String eventoId, String localId, TipoEvento tipoEvento,
                                             PorteEvento porteEvento, int participantesContexto,
                                             String notasPorCriterio, double notaFinal, String justificativa,
                                             String usuarioResponsavel, LocalDateTime dataHoraRegistro) {
        this.id = id;
        this.eventoId = eventoId;
        this.localId = localId;
        this.tipoEvento = tipoEvento;
        this.porteEvento = porteEvento;
        this.participantesContexto = participantesContexto;
        this.notasPorCriterio = notasPorCriterio;
        this.notaFinal = notaFinal;
        this.justificativa = justificativa;
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHoraRegistro = dataHoraRegistro;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getLocalId() { return localId; }
    public TipoEvento getTipoEvento() { return tipoEvento; }
    public PorteEvento getPorteEvento() { return porteEvento; }
    public int getParticipantesContexto() { return participantesContexto; }
    public String getNotasPorCriterio() { return notasPorCriterio; }
    public double getNotaFinal() { return notaFinal; }
    public String getJustificativa() { return justificativa; }
    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
}

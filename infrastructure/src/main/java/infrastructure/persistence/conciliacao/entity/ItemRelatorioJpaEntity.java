package infrastructure.persistence.conciliacao.entity;

import domain.conciliacao.valueobject.MetodoConciliacao;
import domain.conciliacao.valueobject.StatusConciliacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_relatorio_conciliacao")
public class ItemRelatorioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private RelatorioConciliacaoJpaEntity relatorio;

    @Column(name = "despesa_id", nullable = false)
    private String despesaId;

    @Column(name = "contrato_id")
    private String contratoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConciliacao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo")
    private MetodoConciliacao metodo;

    protected ItemRelatorioJpaEntity() {}

    public ItemRelatorioJpaEntity(String id, RelatorioConciliacaoJpaEntity relatorio,
            String despesaId, String contratoId, StatusConciliacao status, MetodoConciliacao metodo) {
        this.id = id;
        this.relatorio = relatorio;
        this.despesaId = despesaId;
        this.contratoId = contratoId;
        this.status = status;
        this.metodo = metodo;
    }

    public String getId() { return id; }
    public RelatorioConciliacaoJpaEntity getRelatorio() { return relatorio; }
    public String getDespesaId() { return despesaId; }
    public String getContratoId() { return contratoId; }
    public StatusConciliacao getStatus() { return status; }
    public MetodoConciliacao getMetodo() { return metodo; }
}

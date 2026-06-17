package infrastructure.persistence.estoque.entity;

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
@Table(name = "alocacao_redistribuicao")
public class AlocacaoRedistribuicaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "item_estoque_id", nullable = false)
    private String itemEstoqueId;

    @Column(name = "quantidade_anterior", nullable = false)
    private int quantidadeAnterior;

    @Column(name = "quantidade_redistribuida", nullable = false)
    private int quantidadeRedistribuida;

    @Column(name = "item_substituto_id")
    private String itemSubstitutoId;

    @Column(name = "quantidade_substituto", nullable = false)
    private int quantidadeSubstituto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alocacao", nullable = false)
    private TipoAlocacaoRedistribuicao tipoAlocacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cenario_id", nullable = false)
    private CenarioRedistribuicaoJpaEntity cenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historico_id")
    private CenarioHistoricoJpaEntity historico;

    protected AlocacaoRedistribuicaoJpaEntity() {
    }

    public AlocacaoRedistribuicaoJpaEntity(String id, String eventoId, String itemEstoqueId,
                                           int quantidadeAnterior, int quantidadeRedistribuida,
                                           String itemSubstitutoId, int quantidadeSubstituto,
                                           TipoAlocacaoRedistribuicao tipoAlocacao,
                                           CenarioRedistribuicaoJpaEntity cenario,
                                           CenarioHistoricoJpaEntity historico) {
        this.id = id;
        this.eventoId = eventoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeRedistribuida = quantidadeRedistribuida;
        this.itemSubstitutoId = itemSubstitutoId;
        this.quantidadeSubstituto = quantidadeSubstituto;
        this.tipoAlocacao = tipoAlocacao;
        this.cenario = cenario;
        this.historico = historico;
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidadeAnterior() {
        return quantidadeAnterior;
    }

    public int getQuantidadeRedistribuida() {
        return quantidadeRedistribuida;
    }

    public String getItemSubstitutoId() {
        return itemSubstitutoId;
    }

    public int getQuantidadeSubstituto() {
        return quantidadeSubstituto;
    }

    public TipoAlocacaoRedistribuicao getTipoAlocacao() {
        return tipoAlocacao;
    }

    public CenarioRedistribuicaoJpaEntity getCenario() {
        return cenario;
    }

    public CenarioHistoricoJpaEntity getHistorico() {
        return historico;
    }
}

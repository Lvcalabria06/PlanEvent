package infrastructure.persistence.estoque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_previsao")
public class ItemPrevisaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "item_estoque_id", nullable = false)
    private String itemEstoqueId;

    @Column(name = "categoria_consumo", nullable = false)
    private String categoriaConsumo;

    @Column(name = "quantidade_estimada", nullable = false)
    private int quantidadeEstimada;

    @Column(name = "quantidade_minima", nullable = false)
    private int quantidadeMinima;

    @Column(name = "quantidade_maxima", nullable = false)
    private int quantidadeMaxima;

    @Column(name = "quantidade_final", nullable = false)
    private int quantidadeFinal;

    @Column(name = "explicacao_calculo", columnDefinition = "TEXT")
    private String explicacaoCalculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previsao_id", nullable = false)
    private PrevisaoConsumoJpaEntity previsao;

    protected ItemPrevisaoJpaEntity() {
    }

    public ItemPrevisaoJpaEntity(String id, String itemEstoqueId, String categoriaConsumo,
                                 int quantidadeEstimada, int quantidadeMinima, int quantidadeMaxima,
                                 int quantidadeFinal, String explicacaoCalculo,
                                 PrevisaoConsumoJpaEntity previsao) {
        this.id = id;
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeEstimada = quantidadeEstimada;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeMaxima = quantidadeMaxima;
        this.quantidadeFinal = quantidadeFinal;
        this.explicacaoCalculo = explicacaoCalculo;
        this.previsao = previsao;
    }

    public String getId() {
        return id;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public String getCategoriaConsumo() {
        return categoriaConsumo;
    }

    public int getQuantidadeEstimada() {
        return quantidadeEstimada;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public int getQuantidadeMaxima() {
        return quantidadeMaxima;
    }

    public int getQuantidadeFinal() {
        return quantidadeFinal;
    }

    public String getExplicacaoCalculo() {
        return explicacaoCalculo;
    }

    public PrevisaoConsumoJpaEntity getPrevisao() {
        return previsao;
    }
}

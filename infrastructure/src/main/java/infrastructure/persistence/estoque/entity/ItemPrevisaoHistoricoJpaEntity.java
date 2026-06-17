package infrastructure.persistence.estoque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ItemPrevisaoHistoricoJpaEntity {

    @Column(name = "item_estoque_id", nullable = false)
    private String itemEstoqueId;

    @Column(name = "categoria_consumo", nullable = false)
    private String categoriaConsumo;

    @Column(name = "quantidade_estimada", nullable = false)
    private int quantidadeEstimada;

    @Column(name = "quantidade_final", nullable = false)
    private int quantidadeFinal;

    protected ItemPrevisaoHistoricoJpaEntity() {
    }

    public ItemPrevisaoHistoricoJpaEntity(String itemEstoqueId, String categoriaConsumo,
                                          int quantidadeEstimada, int quantidadeFinal) {
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeEstimada = quantidadeEstimada;
        this.quantidadeFinal = quantidadeFinal;
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

    public int getQuantidadeFinal() {
        return quantidadeFinal;
    }
}

package infrastructure.persistence.estoque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_consumo_evento")
public class ItemConsumoEventoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "item_estoque_id", nullable = false)
    private String itemEstoqueId;

    @Column(name = "categoria_consumo", nullable = false)
    private String categoriaConsumo;

    @Column(name = "quantidade_consumida", nullable = false)
    private int quantidadeConsumida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumo_evento_id", nullable = false)
    private ConsumoEventoJpaEntity consumoEvento;

    protected ItemConsumoEventoJpaEntity() {
    }

    public ItemConsumoEventoJpaEntity(String id, String itemEstoqueId, String categoriaConsumo,
                                        int quantidadeConsumida, ConsumoEventoJpaEntity consumoEvento) {
        this.id = id;
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeConsumida = quantidadeConsumida;
        this.consumoEvento = consumoEvento;
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

    public int getQuantidadeConsumida() {
        return quantidadeConsumida;
    }

    public ConsumoEventoJpaEntity getConsumoEvento() {
        return consumoEvento;
    }
}

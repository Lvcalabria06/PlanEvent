package infrastructure.persistence.estoque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_reserva")
public class ItemReservaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "item_estoque_id", nullable = false)
    private String itemEstoqueId;

    @Column(name = "quantidade", nullable = false)
    private int quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private ReservaEstoqueJpaEntity reserva;

    protected ItemReservaJpaEntity() {
    }

    public ItemReservaJpaEntity(String id, String itemEstoqueId, int quantidade, ReservaEstoqueJpaEntity reserva) {
        this.id = id;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
        this.reserva = reserva;
    }

    public String getId() {
        return id;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public ReservaEstoqueJpaEntity getReserva() {
        return reserva;
    }
}

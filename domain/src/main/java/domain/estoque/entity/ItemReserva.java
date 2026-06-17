package domain.estoque.entity;

import java.util.UUID;

public class ItemReserva {
    private final String id;
    private final String reservaId;
    private final String itemEstoqueId;
    private int quantidade;

    public ItemReserva(String reservaId, String itemEstoqueId, int quantidade) {
        if (reservaId == null || reservaId.isBlank() || itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("IDs de reserva e item de estoque sao obrigatorios.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.reservaId = reservaId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    private ItemReserva(String id, String reservaId, String itemEstoqueId, int quantidade) {
        this.id = id;
        this.reservaId = reservaId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public static ItemReserva reconstituir(String id, String reservaId, String itemEstoqueId, int quantidade) {
        return new ItemReserva(id, reservaId, itemEstoqueId, quantidade);
    }

    public void atualizarQuantidade(int novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.quantidade = novaQuantidade;
    }

    public String getId() {
        return id;
    }

    public String getReservaId() {
        return reservaId;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}

package domain.estoque.entity;

import java.util.UUID;

public class ItemReserva {
    private final String id;
    private final String reservaId;
    private final String itemEstoqueId;
    private int quantidade;

    public ItemReserva() {
        this.id = UUID.randomUUID().toString();
        this.reservaId = null;
        this.itemEstoqueId = null;
    }

    public ItemReserva(String reservaId, String itemEstoqueId, int quantidade) {
        if (reservaId == null || itemEstoqueId == null) {
            throw new IllegalArgumentException("IDs de reserva e item de estoque são obrigatórios.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.reservaId = reservaId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public void atualizarQuantidade(int novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.quantidade = novaQuantidade;
    }

    // Getters
    public String getId() { return id; }
    public String getReservaId() { return reservaId; }
    public String getItemEstoqueId() { return itemEstoqueId; }
    public int getQuantidade() { return quantidade; }
}

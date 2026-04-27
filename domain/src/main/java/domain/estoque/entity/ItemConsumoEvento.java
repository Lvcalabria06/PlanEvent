package domain.estoque.entity;

import java.util.UUID;

public class ItemConsumoEvento {
    private final String id;
    private final String itemEstoqueId;
    private final int quantidadeConsumida;

    public ItemConsumoEvento(String itemEstoqueId, int quantidadeConsumida) {
        if (itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("ID do item de estoque e obrigatorio.");
        }
        if (quantidadeConsumida <= 0) {
            throw new IllegalArgumentException("Quantidade consumida deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadeConsumida = quantidadeConsumida;
    }

    public String getId() {
        return id;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidadeConsumida() {
        return quantidadeConsumida;
    }
}

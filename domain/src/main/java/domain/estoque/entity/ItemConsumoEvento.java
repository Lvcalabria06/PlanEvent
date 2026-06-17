package domain.estoque.entity;

import java.util.UUID;

public class ItemConsumoEvento {
    private final String id;
    private final String itemEstoqueId;
    private final String categoriaConsumo;
    private final int quantidadeConsumida;

    public ItemConsumoEvento(String itemEstoqueId, int quantidadeConsumida) {
        this(itemEstoqueId, itemEstoqueId, quantidadeConsumida);
    }

    public ItemConsumoEvento(String itemEstoqueId, String categoriaConsumo, int quantidadeConsumida) {
        if (itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("Item de estoque e obrigatorio.");
        }
        if (categoriaConsumo == null || categoriaConsumo.isBlank()) {
            throw new IllegalArgumentException("Categoria de consumo e obrigatoria.");
        }
        if (quantidadeConsumida <= 0) {
            throw new IllegalArgumentException("Quantidade consumida deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeConsumida = quantidadeConsumida;
    }

    private ItemConsumoEvento(String id, String itemEstoqueId, String categoriaConsumo, int quantidadeConsumida) {
        this.id = id;
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeConsumida = quantidadeConsumida;
    }

    public static ItemConsumoEvento reconstituir(String id, String itemEstoqueId, String categoriaConsumo,
                                                 int quantidadeConsumida) {
        return new ItemConsumoEvento(id, itemEstoqueId, categoriaConsumo, quantidadeConsumida);
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
}

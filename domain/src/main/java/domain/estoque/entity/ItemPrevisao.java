package domain.estoque.entity;

import java.util.UUID;

public class ItemPrevisao {
    private final String id;
    private final String previsaoId;
    private final String itemEstoqueId;
    private int quantidadePrevista;
    private int quantidadeAjustada;

    public ItemPrevisao() {
        this.id = UUID.randomUUID().toString();
        this.previsaoId = null;
        this.itemEstoqueId = null;
    }

    public ItemPrevisao(String previsaoId, String itemEstoqueId, int quantidadePrevista) {
        if (previsaoId == null || itemEstoqueId == null) {
            throw new IllegalArgumentException("IDs de previsão e item de estoque são obrigatórios.");
        }
        if (quantidadePrevista < 0) {
            throw new IllegalArgumentException("A quantidade prevista não pode ser negativa.");
        }
        this.id = UUID.randomUUID().toString();
        this.previsaoId = previsaoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadePrevista = quantidadePrevista;
        this.quantidadeAjustada = quantidadePrevista;
    }

    public void ajustarQuantidade(int novaQuantidadeAjustada) {
        if (novaQuantidadeAjustada < 0) {
            throw new IllegalArgumentException("A quantidade ajustada não pode ser negativa.");
        }
        this.quantidadeAjustada = novaQuantidadeAjustada;
    }

    // Getters
    public String getId() { return id; }
    public String getPrevisaoId() { return previsaoId; }
    public String getItemEstoqueId() { return itemEstoqueId; }
    public int getQuantidadePrevista() { return quantidadePrevista; }
    public int getQuantidadeAjustada() { return quantidadeAjustada; }
}

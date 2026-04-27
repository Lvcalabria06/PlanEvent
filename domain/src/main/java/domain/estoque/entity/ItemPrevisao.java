package domain.estoque.entity;

import java.util.UUID;

public class ItemPrevisao {
    private final String id;
    private final String previsaoId;
    private final String itemEstoqueId;
    private final int quantidadePrevista;
    private int quantidadeAjustada;

    public ItemPrevisao(String previsaoId, String itemEstoqueId, int quantidadePrevista) {
        if (previsaoId == null || previsaoId.isBlank() || itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("IDs de previsao e item de estoque sao obrigatorios.");
        }
        if (quantidadePrevista < 0) {
            throw new IllegalArgumentException("A quantidade prevista nao pode ser negativa.");
        }
        this.id = UUID.randomUUID().toString();
        this.previsaoId = previsaoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadePrevista = quantidadePrevista;
        this.quantidadeAjustada = quantidadePrevista;
    }

    public ItemPrevisao(ItemPrevisao origem) {
        this.id = UUID.randomUUID().toString();
        this.previsaoId = origem.previsaoId;
        this.itemEstoqueId = origem.itemEstoqueId;
        this.quantidadePrevista = origem.quantidadePrevista;
        this.quantidadeAjustada = origem.quantidadeAjustada;
    }

    public void ajustarQuantidade(int novaQuantidadeAjustada) {
        if (novaQuantidadeAjustada < 0) {
            throw new IllegalArgumentException("A quantidade ajustada nao pode ser negativa.");
        }
        this.quantidadeAjustada = novaQuantidadeAjustada;
    }

    public String getId() {
        return id;
    }

    public String getPrevisaoId() {
        return previsaoId;
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidadePrevista() {
        return quantidadePrevista;
    }

    public int getQuantidadeAjustada() {
        return quantidadeAjustada;
    }
}

package domain.estoque.entity;

import java.util.UUID;

public class ItemPrevisao {
    private final String id;
    private final String previsaoId;
    private final String itemEstoqueId;
    private final String categoriaConsumo;
    private final int quantidadeEstimada;
    private final int quantidadeMinima;
    private final int quantidadeMaxima;
    private final String explicacaoCalculo;
    private int quantidadeFinal;

    public ItemPrevisao(String previsaoId,
                        String itemEstoqueId,
                        String categoriaConsumo,
                        int quantidadeEstimada,
                        int quantidadeMinima,
                        int quantidadeMaxima,
                        String explicacaoCalculo) {
        if (previsaoId == null || previsaoId.isBlank()) {
            throw new IllegalArgumentException("Previsao e obrigatoria.");
        }
        if (itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("Item de estoque e obrigatorio.");
        }
        if (categoriaConsumo == null || categoriaConsumo.isBlank()) {
            throw new IllegalArgumentException("Categoria de consumo e obrigatoria.");
        }
        if (quantidadeEstimada < 0 || quantidadeMinima < 0 || quantidadeMaxima < quantidadeMinima) {
            throw new IllegalArgumentException("Faixa de previsao invalida.");
        }
        this.id = UUID.randomUUID().toString();
        this.previsaoId = previsaoId;
        this.itemEstoqueId = itemEstoqueId;
        this.categoriaConsumo = categoriaConsumo;
        this.quantidadeEstimada = quantidadeEstimada;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeMaxima = quantidadeMaxima;
        this.explicacaoCalculo = explicacaoCalculo;
        this.quantidadeFinal = quantidadeEstimada;
    }

    public void sobrescreverQuantidadeFinal(int novaQuantidade) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade final nao pode ser negativa.");
        }
        this.quantidadeFinal = novaQuantidade;
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

    public String getExplicacaoCalculo() {
        return explicacaoCalculo;
    }

    public int getQuantidadeFinal() {
        return quantidadeFinal;
    }
}

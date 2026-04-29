package domain.estoque.entity;

public class ItemPrevisaoHistorico {
    private final String itemEstoqueId;
    private final String categoriaConsumo;
    private final int quantidadeEstimada;
    private final int quantidadeFinal;

    public ItemPrevisaoHistorico(String itemEstoqueId, String categoriaConsumo, int quantidadeEstimada, int quantidadeFinal) {
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

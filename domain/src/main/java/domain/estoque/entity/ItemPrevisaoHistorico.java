package domain.estoque.entity;

public class ItemPrevisaoHistorico {
    private final String itemEstoqueId;
    private final int quantidadePrevista;
    private final int quantidadeAjustada;

    public ItemPrevisaoHistorico(String itemEstoqueId, int quantidadePrevista, int quantidadeAjustada) {
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadePrevista = quantidadePrevista;
        this.quantidadeAjustada = quantidadeAjustada;
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

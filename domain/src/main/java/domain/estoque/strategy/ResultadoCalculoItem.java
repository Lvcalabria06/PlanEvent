package domain.estoque.strategy;

import domain.estoque.entity.ItemPrevisao;

public class ResultadoCalculoItem {

    private final ItemPrevisao itemPrevisao;
    private final boolean fallbackAplicado;

    public ResultadoCalculoItem(ItemPrevisao itemPrevisao, boolean fallbackAplicado) {
        this.itemPrevisao = itemPrevisao;
        this.fallbackAplicado = fallbackAplicado;
    }

    public ItemPrevisao getItemPrevisao() {
        return itemPrevisao;
    }

    public boolean isFallbackAplicado() {
        return fallbackAplicado;
    }
}

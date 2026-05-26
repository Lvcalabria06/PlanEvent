package domain.estoque.strategy;

import java.util.Collections;
import java.util.List;

public class ContextoCalculoItem {

    private final String previsaoId;
    private final String itemId;
    private final String categoria;
    private final List<RegistroHistoricoNormalizado> historicosDoItem;
    private final List<RegistroHistoricoNormalizado> historicosGlobais;

    public ContextoCalculoItem(String previsaoId,
                               String itemId,
                               String categoria,
                               List<RegistroHistoricoNormalizado> historicosDoItem,
                               List<RegistroHistoricoNormalizado> historicosGlobais) {
        this.previsaoId = previsaoId;
        this.itemId = itemId;
        this.categoria = categoria;
        this.historicosDoItem = historicosDoItem == null
                ? Collections.emptyList()
                : List.copyOf(historicosDoItem);
        this.historicosGlobais = historicosGlobais == null
                ? Collections.emptyList()
                : List.copyOf(historicosGlobais);
    }

    public String getPrevisaoId() { return previsaoId; }
    public String getItemId() { return itemId; }
    public String getCategoria() { return categoria; }
    public List<RegistroHistoricoNormalizado> getHistoricosDoItem() { return historicosDoItem; }
    public List<RegistroHistoricoNormalizado> getHistoricosGlobais() { return historicosGlobais; }
}

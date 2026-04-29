package domain.estoque.entity;

import java.util.UUID;

public class ItemSubstituicao {
    private final String id;
    private final String itemOriginalId;
    private final String itemSubstitutoId;
    private final double fatorEquivalencia;

    public ItemSubstituicao(String itemOriginalId, String itemSubstitutoId, double fatorEquivalencia) {
        if (itemOriginalId == null || itemOriginalId.isBlank()) {
            throw new IllegalArgumentException("Item original e obrigatorio.");
        }
        if (itemSubstitutoId == null || itemSubstitutoId.isBlank()) {
            throw new IllegalArgumentException("Item substituto e obrigatorio.");
        }
        if (fatorEquivalencia <= 0) {
            throw new IllegalArgumentException("Fator de equivalencia deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.itemOriginalId = itemOriginalId;
        this.itemSubstitutoId = itemSubstitutoId;
        this.fatorEquivalencia = fatorEquivalencia;
    }

    public String getId() { return id; }
    public String getItemOriginalId() { return itemOriginalId; }
    public String getItemSubstitutoId() { return itemSubstitutoId; }
    public double getFatorEquivalencia() { return fatorEquivalencia; }
}

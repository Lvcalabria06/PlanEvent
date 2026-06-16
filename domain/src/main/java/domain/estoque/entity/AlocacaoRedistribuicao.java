package domain.estoque.entity;

import java.util.UUID;

public class AlocacaoRedistribuicao {
    private final String id;
    private final String eventoId;
    private final String itemEstoqueId;
    private final int quantidadeAnterior;
    private int quantidadeRedistribuida;
    private String itemSubstitutoId;
    private int quantidadeSubstituto;

    public AlocacaoRedistribuicao(String eventoId,
                                   String itemEstoqueId,
                                   int quantidadeAnterior,
                                   int quantidadeRedistribuida) {
        if (eventoId == null || eventoId.isBlank()) {
            throw new IllegalArgumentException("ID do evento e obrigatorio.");
        }
        if (itemEstoqueId == null || itemEstoqueId.isBlank()) {
            throw new IllegalArgumentException("ID do item de estoque e obrigatorio.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeRedistribuida = quantidadeRedistribuida;
        this.quantidadeSubstituto = 0;
    }

    public void aplicarSubstituicao(String itemSubstitutoId, int quantidadeSubstituto) {
        if (itemSubstitutoId == null || itemSubstitutoId.isBlank()) {
            throw new IllegalArgumentException("Item substituto e obrigatorio para aplicar substituicao.");
        }
        if (quantidadeSubstituto <= 0) {
            throw new IllegalArgumentException("Quantidade do substituto deve ser maior que zero.");
        }
        this.itemSubstitutoId = itemSubstitutoId;
        this.quantidadeSubstituto = quantidadeSubstituto;
    }

    public void ajustarQuantidadeRedistribuida(int novaQuantidade) {
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade redistribuida nao pode ser negativa.");
        }
        this.quantidadeRedistribuida = novaQuantidade;
    }

    public int getDiferenca() {
        return quantidadeRedistribuida - quantidadeAnterior;
    }

    public boolean possuiSubstituicao() {
        return itemSubstitutoId != null && quantidadeSubstituto > 0;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getItemEstoqueId() { return itemEstoqueId; }
    public int getQuantidadeAnterior() { return quantidadeAnterior; }
    public int getQuantidadeRedistribuida() { return quantidadeRedistribuida; }
    public String getItemSubstitutoId() { return itemSubstitutoId; }
    public int getQuantidadeSubstituto() { return quantidadeSubstituto; }
}

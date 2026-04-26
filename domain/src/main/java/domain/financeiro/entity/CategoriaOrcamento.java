package domain.financeiro.entity;

import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.util.UUID;

public class CategoriaOrcamento {

    private final String id;
    private final String orcamentoId;
    private final CategoriaDespesa nome;
    private BigDecimal valorPrevisto;

    public CategoriaOrcamento(String orcamentoId, CategoriaDespesa nome, BigDecimal valorPrevisto) {
        if (orcamentoId == null || orcamentoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do orçamento é obrigatório.");
        }
        if (nome == null) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório.");
        }
        if (valorPrevisto == null || valorPrevisto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor previsto não pode ser nulo ou negativo.");
        }
        this.id = UUID.randomUUID().toString();
        this.orcamentoId = orcamentoId;
        this.nome = nome;
        this.valorPrevisto = valorPrevisto;
    }

    public void ajustarValorPrevisto(BigDecimal novoValorPrevisto) {
        if (novoValorPrevisto == null || novoValorPrevisto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Novo valor previsto não pode ser nulo ou negativo.");
        }
        this.valorPrevisto = novoValorPrevisto;
    }

    // Getters
    public String getId() { return id; }
    public String getOrcamentoId() { return orcamentoId; }
    public CategoriaDespesa getNome() { return nome; }
    public BigDecimal getValorPrevisto() { return valorPrevisto; }
}

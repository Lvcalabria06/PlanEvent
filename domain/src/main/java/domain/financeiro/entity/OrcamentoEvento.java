package domain.financeiro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrcamentoEvento {
    private final String id;
    private final String eventoId;
    private BigDecimal valorTotal;
    private final LocalDateTime dataCriacao;

    public OrcamentoEvento() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.dataCriacao = LocalDateTime.now();
    }

    public OrcamentoEvento(String eventoId, BigDecimal valorTotal) {
        if (eventoId == null) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor total não pode ser negativo.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.valorTotal = valorTotal;
        this.dataCriacao = LocalDateTime.now();
    }

    public void atualizarValorTotal(BigDecimal novoValorTotal) {
        if (novoValorTotal == null || novoValorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor total não pode ser negativo.");
        }
        this.valorTotal = novoValorTotal;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}

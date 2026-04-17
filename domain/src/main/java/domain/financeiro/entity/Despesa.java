package domain.financeiro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Despesa {
    private final String id;
    private final String eventoId;
    private final String categoriaId;
    private final String fornecedorId;
    private BigDecimal valor;
    private LocalDateTime data;

    public Despesa() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.categoriaId = null;
        this.fornecedorId = null;
    }

    public Despesa(String eventoId, String categoriaId, String fornecedorId, BigDecimal valor, LocalDateTime data) {
        if (eventoId == null || categoriaId == null || fornecedorId == null) {
            throw new IllegalArgumentException("IDs de evento, categoria e fornecedor são obrigatórios.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da despesa deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.categoriaId = categoriaId;
        this.fornecedorId = fornecedorId;
        this.valor = valor;
        this.data = (data != null) ? data : LocalDateTime.now();
    }
    
    public void corrigirValor(BigDecimal novoValor) {
        if (novoValor == null || novoValor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Novo valor da despesa deve ser maior que zero.");
        }
        this.valor = novoValor;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getCategoriaId() { return categoriaId; }
    public String getFornecedorId() { return fornecedorId; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getData() { return data; }
}

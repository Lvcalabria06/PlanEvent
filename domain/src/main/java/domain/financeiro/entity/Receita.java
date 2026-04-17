package domain.financeiro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Receita {
    private final String id;
    private final String eventoId;
    private String descricao;
    private BigDecimal valor;
    private LocalDateTime data;

    public Receita() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
    }

    public Receita(String eventoId, String descricao, BigDecimal valor, LocalDateTime data) {
        if (eventoId == null) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da receita é obrigatória.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da receita deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.descricao = descricao;
        this.valor = valor;
        this.data = (data != null) ? data : LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getData() { return data; }
}

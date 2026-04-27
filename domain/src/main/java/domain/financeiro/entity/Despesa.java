package domain.financeiro.entity;

import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Despesa {

    private final String id;
    private final String eventoId;
    private final CategoriaDespesa categoria;
    private final String fornecedorId;
    private BigDecimal valor;
    private LocalDateTime data;
    private final String lancadoPorUsuarioId;
    private final LocalDateTime dataHoraLancamento;

    
    public Despesa(String eventoId,
                   CategoriaDespesa categoria,
                   String fornecedorId,
                   BigDecimal valor,
                   LocalDateTime data,
                   String lancadoPorUsuarioId) {

        
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria da despesa é obrigatória.");
        }
        
        if (fornecedorId == null || fornecedorId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do fornecedor é obrigatório.");
        }
        
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da despesa deve ser maior que zero.");
        }
        
        if (data == null) {
            throw new IllegalArgumentException("Data da despesa é obrigatória.");
        }
        
        if (lancadoPorUsuarioId == null || lancadoPorUsuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário responsável pelo lançamento é obrigatório.");
        }

        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.categoria = categoria;
        this.fornecedorId = fornecedorId;
        this.valor = valor;
        this.data = data;
        this.lancadoPorUsuarioId = lancadoPorUsuarioId;
        this.dataHoraLancamento = LocalDateTime.now(); // RN9
    }

    public void corrigirValor(BigDecimal novoValor) {
        if (novoValor == null || novoValor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Novo valor da despesa deve ser maior que zero.");
        }
        this.valor = novoValor;
    }

    //Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public CategoriaDespesa getCategoria() { return categoria; }
    public String getFornecedorId() { return fornecedorId; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getData() { return data; }
    public String getLancadoPorUsuarioId() { return lancadoPorUsuarioId; }
    public LocalDateTime getDataHoraLancamento() { return dataHoraLancamento; }
}

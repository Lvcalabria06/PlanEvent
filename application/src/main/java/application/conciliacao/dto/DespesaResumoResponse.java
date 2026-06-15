package application.conciliacao.dto;

import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.StatusDespesa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DespesaResumoResponse(
        String id,
        String eventoId,
        CategoriaDespesa categoria,
        String fornecedorId,
        BigDecimal valor,
        LocalDateTime data,
        StatusDespesa status
) {}

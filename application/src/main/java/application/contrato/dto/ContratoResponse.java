package application.contrato.dto;

import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ContratoResponse(
        String id,
        String eventoId,
        String fornecedorId,
        TipoContrato tipo,
        String objeto,
        BigDecimal valor,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        StatusContrato status,
        List<ParteContratoDto> partes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

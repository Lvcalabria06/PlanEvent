package application.contrato.dto;

import domain.contrato.valueobject.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CriarContratoRequest(
        String eventoId,
        String fornecedorId,
        TipoContrato tipo,
        String objeto,
        BigDecimal valor,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        List<ParteContratoDto> partes
) {}

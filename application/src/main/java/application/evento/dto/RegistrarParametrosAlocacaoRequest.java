package application.evento.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegistrarParametrosAlocacaoRequest(
        BigDecimal tetoCusto,
        LocalDateTime dataInicio,
        LocalDateTime dataFim
) {}

package application.evento.dto;

import java.math.BigDecimal;

public record FixarLocalPrincipalRequest(
        String localId,
        BigDecimal tetoCusto
) {}

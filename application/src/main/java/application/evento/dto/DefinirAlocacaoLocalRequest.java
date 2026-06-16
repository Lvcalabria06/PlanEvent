package application.evento.dto;

import java.math.BigDecimal;
import java.util.List;

public record DefinirAlocacaoLocalRequest(
        String localPrincipalId,
        BigDecimal tetoCusto,
        List<String> localIdsContingenciaOrdenados
) {}

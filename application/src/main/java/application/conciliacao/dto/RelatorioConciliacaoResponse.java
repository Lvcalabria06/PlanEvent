package application.conciliacao.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RelatorioConciliacaoResponse(
        String id,
        String eventoId,
        String responsavelId,
        LocalDateTime dataGeracao,
        List<ItemRelatorioResponse> itens
) {}

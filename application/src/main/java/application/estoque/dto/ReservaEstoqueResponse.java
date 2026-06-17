package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReservaEstoqueResponse(
        String id,
        String eventoId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String status,
        List<ItemReservaResponse> itensReservados) {
}

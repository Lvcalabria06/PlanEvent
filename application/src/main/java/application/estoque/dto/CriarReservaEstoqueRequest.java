package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CriarReservaEstoqueRequest(
        String eventoId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        List<ItemReservaRequest> itens) {
}

package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AtualizarReservaEstoqueRequest(
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        List<ItemReservaRequest> itens) {
}

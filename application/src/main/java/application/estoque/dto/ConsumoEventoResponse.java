package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ConsumoEventoResponse(
        String id,
        String eventoId,
        String registradoPorUsuarioId,
        LocalDateTime dataRegistro,
        boolean valido,
        List<ItemConsumoResponse> itensConsumidos) {
}

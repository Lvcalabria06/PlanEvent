package application.estoque.dto;

import java.util.List;

public record RegistrarConsumoEventoRequest(
        String eventoId,
        String usuarioId,
        List<ItemConsumoRequest> itens) {
}

package application.estoque.dto;

import java.util.List;

public record ItemPrevisaoHistoricoResponse(
        String itemEstoqueId,
        String categoriaConsumo,
        int quantidadeEstimada,
        int quantidadeFinal) {
}

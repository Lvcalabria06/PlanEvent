package application.estoque.dto;

public record ItemConsumoResponse(
        String id,
        String itemEstoqueId,
        String categoriaConsumo,
        int quantidadeConsumida) {
}

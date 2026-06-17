package application.estoque.dto;

public record ItemPrevisaoResponse(
        String id,
        String itemEstoqueId,
        String categoriaConsumo,
        int quantidadeEstimada,
        int quantidadeMinima,
        int quantidadeMaxima,
        int quantidadeFinal,
        String explicacaoCalculo) {
}

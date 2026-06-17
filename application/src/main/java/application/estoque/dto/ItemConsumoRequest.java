package application.estoque.dto;

public record ItemConsumoRequest(String itemEstoqueId, String categoriaConsumo, int quantidadeConsumida) {
}

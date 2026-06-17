package application.estoque.dto;

public record AlocacaoRedistribuicaoResponse(
        String id,
        String eventoId,
        String itemEstoqueId,
        int quantidadeAnterior,
        int quantidadeRedistribuida,
        String itemSubstitutoId,
        int quantidadeSubstituto) {
}

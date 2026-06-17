package application.estoque.dto;

import java.util.List;

public record ImpactoItemResponse(
        String itemEstoqueId,
        int quantidadeAnterior,
        int quantidadeRedistribuida,
        int deficit,
        int excesso) {
}

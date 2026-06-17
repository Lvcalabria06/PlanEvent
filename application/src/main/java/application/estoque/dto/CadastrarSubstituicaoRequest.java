package application.estoque.dto;

public record CadastrarSubstituicaoRequest(
        String itemOriginalId,
        String itemSubstitutoId,
        double fatorEquivalencia) {
}

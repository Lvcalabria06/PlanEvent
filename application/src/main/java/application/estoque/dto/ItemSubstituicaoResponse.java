package application.estoque.dto;

public record ItemSubstituicaoResponse(
        String id,
        String itemOriginalId,
        String itemSubstitutoId,
        double fatorEquivalencia) {
}

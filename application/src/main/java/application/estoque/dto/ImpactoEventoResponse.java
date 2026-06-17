package application.estoque.dto;

import java.util.List;

public record ImpactoEventoResponse(String eventoId, List<ImpactoItemResponse> itensImpactados) {
}

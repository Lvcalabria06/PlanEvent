package application.estoque.dto;

import java.util.Map;

public record AjustarPrevisaoRequest(
        Map<String, Integer> quantidadesAjustadas,
        String usuarioId,
        String justificativa) {
}

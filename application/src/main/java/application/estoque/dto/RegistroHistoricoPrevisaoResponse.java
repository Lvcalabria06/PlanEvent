package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RegistroHistoricoPrevisaoResponse(
        String id,
        int versao,
        String tipoRegistro,
        String usuarioResponsavelId,
        LocalDateTime dataHora,
        String justificativa,
        List<ItemPrevisaoHistoricoResponse> itens) {
}

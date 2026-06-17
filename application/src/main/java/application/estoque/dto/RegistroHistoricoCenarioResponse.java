package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RegistroHistoricoCenarioResponse(
        String id,
        String usuarioResponsavelId,
        LocalDateTime dataHora,
        String descricao,
        List<AlocacaoRedistribuicaoResponse> alocacoesSnapshot) {
}

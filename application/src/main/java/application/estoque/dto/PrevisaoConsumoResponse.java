package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PrevisaoConsumoResponse(
        String id,
        String eventoId,
        String geradoPorUsuarioId,
        LocalDateTime dataGeracao,
        String statusHistorico,
        boolean fallbackUtilizado,
        boolean invalidada,
        int versaoAtual,
        int totalEventosBase,
        List<ItemPrevisaoResponse> itens,
        List<RegistroHistoricoPrevisaoResponse> historicoRegistros) {
}

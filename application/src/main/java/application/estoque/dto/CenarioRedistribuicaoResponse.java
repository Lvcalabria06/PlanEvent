package application.estoque.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CenarioRedistribuicaoResponse(
        String id,
        LocalDateTime dataCriacao,
        String geradoPorUsuarioId,
        LocalDateTime periodoInicio,
        LocalDateTime periodoFim,
        String status,
        List<AlocacaoRedistribuicaoResponse> alocacoesAtuais,
        List<AlocacaoRedistribuicaoResponse> alocacoesOtimizadas,
        List<ImpactoEventoResponse> impactosPorEvento,
        List<RegistroHistoricoCenarioResponse> historico,
        LocalDateTime dataAplicacao,
        String aplicadoPorUsuarioId) {
}

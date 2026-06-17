package application.evento.dto;

import domain.evento.valueobject.MotivoAlertaAlocacao;

import java.util.List;

public record AlertaRiscoAlocacaoDto(
        String eventoId,
        String localPrincipalId,
        String descricao,
        List<MotivoAlertaAlocacao> motivos,
        String melhorSubstitutoSugeridoId,
        String melhorSubstitutoSugeridoNome
) {}

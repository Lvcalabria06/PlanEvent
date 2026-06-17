package presentationbackend.controller.local;

import domain.local.entity.AvaliacaoContextualLocal;

import java.time.LocalDateTime;
import java.util.Map;

public record AvaliacaoContextualResponse(
        String id,
        String eventoId,
        String localId,
        String tipoEvento,
        String porteEvento,
        int participantesContexto,
        Map<String, Integer> notasPorCriterio,
        double notaFinal,
        String justificativa,
        String usuarioResponsavel,
        LocalDateTime dataHoraRegistro
) {
    public static AvaliacaoContextualResponse de(AvaliacaoContextualLocal avaliacao) {
        return new AvaliacaoContextualResponse(
                avaliacao.getId(),
                avaliacao.getEventoId(),
                avaliacao.getLocalId(),
                avaliacao.getTipoEvento().name(),
                avaliacao.getPorteEvento().name(),
                avaliacao.getParticipantesContexto(),
                avaliacao.getNotasPorCriterio(),
                avaliacao.getNotaFinal(),
                avaliacao.getJustificativa(),
                avaliacao.getUsuarioResponsavel(),
                avaliacao.getDataHoraRegistro()
        );
    }
}

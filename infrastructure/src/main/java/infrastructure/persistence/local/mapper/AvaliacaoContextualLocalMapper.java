package infrastructure.persistence.local.mapper;

import domain.local.entity.AvaliacaoContextualLocal;
import infrastructure.persistence.local.entity.AvaliacaoContextualLocalJpaEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AvaliacaoContextualLocalMapper {

    private AvaliacaoContextualLocalMapper() {}

    public static AvaliacaoContextualLocal paraDominio(AvaliacaoContextualLocalJpaEntity entity) {
        return AvaliacaoContextualLocal.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getLocalId(),
                entity.getTipoEvento(),
                entity.getPorteEvento(),
                entity.getParticipantesContexto(),
                deserializarNotas(entity.getNotasPorCriterio()),
                entity.getNotaFinal(),
                entity.getJustificativa(),
                entity.getUsuarioResponsavel(),
                entity.getDataHoraRegistro()
        );
    }

    public static AvaliacaoContextualLocalJpaEntity paraJpa(AvaliacaoContextualLocal avaliacao) {
        return new AvaliacaoContextualLocalJpaEntity(
                avaliacao.getId(),
                avaliacao.getEventoId(),
                avaliacao.getLocalId(),
                avaliacao.getTipoEvento(),
                avaliacao.getPorteEvento(),
                avaliacao.getParticipantesContexto(),
                serializarNotas(avaliacao.getNotasPorCriterio()),
                avaliacao.getNotaFinal(),
                avaliacao.getJustificativa(),
                avaliacao.getUsuarioResponsavel(),
                avaliacao.getDataHoraRegistro()
        );
    }

    private static String serializarNotas(Map<String, Integer> notas) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : notas.entrySet()) {
            if (sb.length() > 0) sb.append("|");
            sb.append(entry.getKey()).append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    private static Map<String, Integer> deserializarNotas(String texto) {
        Map<String, Integer> notas = new LinkedHashMap<>();
        if (texto == null || texto.isBlank()) return notas;
        for (String entry : texto.split("\\|")) {
            int idx = entry.lastIndexOf(':');
            if (idx > 0) {
                notas.put(entry.substring(0, idx), Integer.parseInt(entry.substring(idx + 1)));
            }
        }
        return notas;
    }
}

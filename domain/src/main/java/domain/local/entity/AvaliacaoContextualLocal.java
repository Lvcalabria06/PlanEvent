package domain.local.entity;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class AvaliacaoContextualLocal {
    private final String id;
    private final String eventoId;
    private final String localId;
    private final TipoEvento tipoEvento;
    private final PorteEvento porteEvento;
    private final int participantesContexto;
    private final Map<String, Integer> notasPorCriterio;
    private final double notaFinal;
    private final String justificativa;
    private final String usuarioResponsavel;
    private final LocalDateTime dataHoraRegistro;

    private AvaliacaoContextualLocal(
            String id,
            String eventoId,
            String localId,
            TipoEvento tipoEvento,
            PorteEvento porteEvento,
            int participantesContexto,
            Map<String, Integer> notasPorCriterio,
            double notaFinal,
            String justificativa,
            String usuarioResponsavel,
            LocalDateTime dataHoraRegistro) {
        this.id = id;
        this.eventoId = eventoId;
        this.localId = localId;
        this.tipoEvento = tipoEvento;
        this.porteEvento = porteEvento;
        this.participantesContexto = participantesContexto;
        this.notasPorCriterio = Collections.unmodifiableMap(notasPorCriterio);
        this.notaFinal = notaFinal;
        this.justificativa = justificativa;
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataHoraRegistro = dataHoraRegistro;
    }

    public static AvaliacaoContextualLocal reconstituir(
            String id,
            String eventoId,
            String localId,
            TipoEvento tipoEvento,
            PorteEvento porteEvento,
            int participantesContexto,
            Map<String, Integer> notasPorCriterio,
            double notaFinal,
            String justificativa,
            String usuarioResponsavel,
            LocalDateTime dataHoraRegistro) {
        return new AvaliacaoContextualLocal(id, eventoId, localId, tipoEvento, porteEvento,
                participantesContexto, notasPorCriterio, notaFinal, justificativa, usuarioResponsavel, dataHoraRegistro);
    }

    public AvaliacaoContextualLocal(
            String eventoId,
            String localId,
            TipoEvento tipoEvento,
            PorteEvento porteEvento,
            int participantesContexto,
            Map<String, Integer> notasPorCriterio,
            String justificativa,
            String usuarioResponsavel) {
        if (eventoId == null || eventoId.isBlank()) {
            throw new IllegalArgumentException("Evento é obrigatório.");
        }
        if (localId == null || localId.isBlank()) {
            throw new IllegalArgumentException("Local é obrigatório.");
        }
        if (tipoEvento == null || porteEvento == null) {
            throw new IllegalArgumentException("Tipo e porte do evento são obrigatórios.");
        }
        if (participantesContexto < 0) {
            throw new IllegalArgumentException("Quantidade de participantes do contexto não pode ser negativa.");
        }
        if (notasPorCriterio == null || notasPorCriterio.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um critério de avaliação.");
        }
        for (Map.Entry<String, Integer> entry : notasPorCriterio.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank()) {
                throw new IllegalArgumentException("Nome do critério é obrigatório.");
            }
            int nota = entry.getValue() == null ? -1 : entry.getValue();
            if (nota < 0 || nota > 5) {
                throw new IllegalArgumentException("Notas devem estar entre 0 e 5.");
            }
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("Justificativa da avaliação é obrigatória.");
        }
        if (usuarioResponsavel == null || usuarioResponsavel.isBlank()) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.localId = localId;
        this.tipoEvento = tipoEvento;
        this.porteEvento = porteEvento;
        this.participantesContexto = participantesContexto;
        this.notasPorCriterio = Collections.unmodifiableMap(notasPorCriterio);
        this.notaFinal = calcularNotaFinal(notasPorCriterio);
        this.justificativa = justificativa.trim();
        this.usuarioResponsavel = usuarioResponsavel.trim();
        this.dataHoraRegistro = LocalDateTime.now();
    }

    private double calcularNotaFinal(Map<String, Integer> notasPorCriterio) {
        double soma = 0;
        for (Integer nota : notasPorCriterio.values()) {
            soma += nota;
        }
        return soma / notasPorCriterio.size();
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getLocalId() {
        return localId;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public PorteEvento getPorteEvento() {
        return porteEvento;
    }

    public int getParticipantesContexto() {
        return participantesContexto;
    }

    public Map<String, Integer> getNotasPorCriterio() {
        return notasPorCriterio;
    }

    public double getNotaFinal() {
        return notaFinal;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public LocalDateTime getDataHoraRegistro() {
        return dataHoraRegistro;
    }
}

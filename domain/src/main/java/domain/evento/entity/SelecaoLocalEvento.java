package domain.evento.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class SelecaoLocalEvento {
    private final String id;
    private final String eventoId;
    private final String localId;
    private double indiceAderencia;
    private String justificativa;
    private final String selecionadoPorUsuarioId;
    private final LocalDateTime dataSelecao;

    public SelecaoLocalEvento() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.localId = null;
        this.selecionadoPorUsuarioId = null;
        this.dataSelecao = LocalDateTime.now();
    }

    public SelecaoLocalEvento(String eventoId, String localId, double indiceAderencia, String justificativa, String selecionadoPorUsuarioId) {
        if (eventoId == null || localId == null || selecionadoPorUsuarioId == null) {
            throw new IllegalArgumentException("IDs de evento, local e usuário são obrigatórios.");
        }
        if (indiceAderencia < 0 || indiceAderencia > 100) {
            throw new IllegalArgumentException("Índice de aderência deve estar entre 0 e 100.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.localId = localId;
        this.indiceAderencia = indiceAderencia;
        this.justificativa = justificativa;
        this.selecionadoPorUsuarioId = selecionadoPorUsuarioId;
        this.dataSelecao = LocalDateTime.now();
    }

    public void reverJustificativa(String novaJustificativa) {
        this.justificativa = novaJustificativa;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getLocalId() { return localId; }
    public double getIndiceAderencia() { return indiceAderencia; }
    public String getJustificativa() { return justificativa; }
    public String getSelecionadoPorUsuarioId() { return selecionadoPorUsuarioId; }
    public LocalDateTime getDataSelecao() { return dataSelecao; }
}

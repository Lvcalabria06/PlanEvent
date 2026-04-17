package domain.local.entity;

import domain.local.valueobject.NivelAdequacao;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvaliacaoLocalEvento {
    private final String id;
    private final String eventoId;
    private final String localId;
    private NivelAdequacao nivelAdequacao;
    private String justificativa;
    private final LocalDateTime dataAvaliacao;

    public AvaliacaoLocalEvento() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.localId = null;
        this.dataAvaliacao = LocalDateTime.now();
    }

    public AvaliacaoLocalEvento(String eventoId, String localId, NivelAdequacao nivelAdequacao, String justificativa) {
        if (eventoId == null || localId == null) {
            throw new IllegalArgumentException("IDs de evento e local são obrigatórios.");
        }
        if (nivelAdequacao == null) {
            throw new IllegalArgumentException("Nível de adequação é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.localId = localId;
        this.nivelAdequacao = nivelAdequacao;
        this.justificativa = justificativa;
        this.dataAvaliacao = LocalDateTime.now();
    }
    
    public void reverAvaliacao(NivelAdequacao novoNivel, String novaJustificativa) {
        if (novoNivel == null) {
            throw new IllegalArgumentException("Novo nível de adequação é obrigatório.");
        }
        this.nivelAdequacao = novoNivel;
        this.justificativa = novaJustificativa;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getLocalId() { return localId; }
    public NivelAdequacao getNivelAdequacao() { return nivelAdequacao; }
    public String getJustificativa() { return justificativa; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
}

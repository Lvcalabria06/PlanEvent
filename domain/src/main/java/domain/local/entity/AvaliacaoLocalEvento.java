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
    private final String registradoPorUsuarioId;

    public AvaliacaoLocalEvento() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.localId = null;
        this.dataAvaliacao = LocalDateTime.now();
        this.registradoPorUsuarioId = null;
    }

    public AvaliacaoLocalEvento(
            String eventoId,
            String localId,
            NivelAdequacao nivelAdequacao,
            String justificativa,
            String registradoPorUsuarioId) {
        if (eventoId == null || localId == null) {
            throw new IllegalArgumentException("IDs de evento e local são obrigatórios.");
        }
        if (nivelAdequacao == null) {
            throw new IllegalArgumentException("Nível de adequação é obrigatório.");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("Justificativa descritiva é obrigatória.");
        }
        if (registradoPorUsuarioId == null || registradoPorUsuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.localId = localId;
        this.nivelAdequacao = nivelAdequacao;
        this.justificativa = justificativa.trim();
        this.dataAvaliacao = LocalDateTime.now();
        this.registradoPorUsuarioId = registradoPorUsuarioId;
    }
    
    public void reverAvaliacao(NivelAdequacao novoNivel, String novaJustificativa) {
        if (novoNivel == null) {
            throw new IllegalArgumentException("Novo nível de adequação é obrigatório.");
        }
        if (novaJustificativa == null || novaJustificativa.isBlank()) {
            throw new IllegalArgumentException("Nova justificativa é obrigatória.");
        }
        this.nivelAdequacao = novoNivel;
        this.justificativa = novaJustificativa.trim();
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getLocalId() { return localId; }
    public NivelAdequacao getNivelAdequacao() { return nivelAdequacao; }
    public String getJustificativa() { return justificativa; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public String getRegistradoPorUsuarioId() { return registradoPorUsuarioId; }
}

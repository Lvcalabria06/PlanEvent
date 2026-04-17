package domain.evento.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlanejamentoEvento {
    private final String id;
    private final String eventoId;
    private String estruturaGerada;
    private final String geradoPorUsuarioId;
    private final LocalDateTime dataGeracao;
    private boolean confirmado;
    private int versao;

    public PlanejamentoEvento() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.geradoPorUsuarioId = null;
        this.dataGeracao = LocalDateTime.now();
        this.versao = 1;
    }

    public PlanejamentoEvento(String eventoId, String estruturaGerada, String geradoPorUsuarioId) {
        if (eventoId == null || geradoPorUsuarioId == null) {
            throw new IllegalArgumentException("Evento e Usuário são obrigatórios para um planejamento.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.estruturaGerada = estruturaGerada;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = LocalDateTime.now();
        this.confirmado = false;
        this.versao = 1;
    }

    public void confirmar() {
        if (this.confirmado) {
            throw new IllegalStateException("Planejamento já foi confirmado.");
        }
        this.confirmado = true;
    }

    public void revisarEstrutura(String novaEstrutura) {
        if (this.confirmado) {
            throw new IllegalStateException("Não é possível revisar uma estrutura de planejamento já confirmada.");
        }
        this.estruturaGerada = novaEstrutura;
        this.versao++;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getEstruturaGerada() { return estruturaGerada; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public boolean isConfirmado() { return confirmado; }
    public int getVersao() { return versao; }
}

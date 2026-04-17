package domain.estoque.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class PrevisaoConsumo {
    private final String id;
    private final String eventoId;
    private final String geradoPorUsuarioId;
    private final LocalDateTime dataGeracao;

    public PrevisaoConsumo() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.geradoPorUsuarioId = null;
        this.dataGeracao = LocalDateTime.now();
    }

    public PrevisaoConsumo(String eventoId, String geradoPorUsuarioId) {
        if (eventoId == null || geradoPorUsuarioId == null) {
            throw new IllegalArgumentException("IDs de evento e usuário são obrigatórios.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
}

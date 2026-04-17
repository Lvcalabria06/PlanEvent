package domain.local.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class IndisponibilidadeLocal {
    private final String id;
    private final String localId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String motivo;

    public IndisponibilidadeLocal() {
        this.id = UUID.randomUUID().toString();
        this.localId = null;
    }

    public IndisponibilidadeLocal(String localId, LocalDateTime dataInicio, LocalDateTime dataFim, String motivo) {
        if (localId == null) {
            throw new IllegalArgumentException("ID do local é obrigatório.");
        }
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Período de indisponibilidade inválido.");
        }
        this.id = UUID.randomUUID().toString();
        this.localId = localId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.motivo = motivo;
    }

    // Getters
    public String getId() { return id; }
    public String getLocalId() { return localId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getMotivo() { return motivo; }
}

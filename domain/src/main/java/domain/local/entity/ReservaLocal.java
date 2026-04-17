package domain.local.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaLocal {
    private final String id;
    private final String agendaLocalId;
    private final String eventoId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public ReservaLocal() {
        this.id = UUID.randomUUID().toString();
        this.agendaLocalId = null;
        this.eventoId = null;
    }

    public ReservaLocal(String agendaLocalId, String eventoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (agendaLocalId == null || eventoId == null) {
            throw new IllegalArgumentException("IDs da agenda e do evento são obrigatórios.");
        }
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Datas inválidas para a reserva.");
        }
        this.id = UUID.randomUUID().toString();
        this.agendaLocalId = agendaLocalId;
        this.eventoId = eventoId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
    
    public void ajustarDatas(LocalDateTime novaDataInicio, LocalDateTime novaDataFim) {
        if (novaDataInicio == null || novaDataFim == null || novaDataInicio.isAfter(novaDataFim)) {
            throw new IllegalArgumentException("Novas datas inválidas.");
        }
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
    }

    // Getters
    public String getId() { return id; }
    public String getAgendaLocalId() { return agendaLocalId; }
    public String getEventoId() { return eventoId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
}

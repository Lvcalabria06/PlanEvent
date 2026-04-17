package domain.estoque.entity;

import domain.estoque.valueobject.StatusReservaEstoque;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaEstoque {
    private final String id;
    private final String eventoId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReservaEstoque status;

    public ReservaEstoque() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
    }

    public ReservaEstoque(String eventoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (eventoId == null) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusReservaEstoque.PENDENTE;
    }

    public void confirmar() {
        if (this.status != StatusReservaEstoque.PENDENTE) {
            throw new IllegalStateException("Apenas reservas pendentes podem ser confirmadas.");
        }
        this.status = StatusReservaEstoque.CONFIRMADA;
    }

    public void cancelar() {
        if (this.status == StatusReservaEstoque.FINALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma reserva já finalizada.");
        }
        this.status = StatusReservaEstoque.CANCELADA;
    }
    
    public void finalizar() {
        if (this.status != StatusReservaEstoque.CONFIRMADA) {
            throw new IllegalStateException("Apenas reservas confirmadas podem ser finalizadas.");
        }
        this.status = StatusReservaEstoque.FINALIZADA;
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusReservaEstoque getStatus() { return status; }
}

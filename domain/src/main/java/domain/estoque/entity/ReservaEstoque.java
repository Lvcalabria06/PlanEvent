package domain.estoque.entity;

import domain.estoque.valueobject.StatusReservaEstoque;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReservaEstoque {
    private final String id;
    private final String eventoId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReservaEstoque status;
    private final List<ItemReserva> itensReservados;

    public ReservaEstoque(String eventoId,
                          LocalDateTime dataInicio,
                          LocalDateTime dataFim,
                          List<ItemReserva> itensReservados) {
        validar(eventoId, dataInicio, dataFim, itensReservados);
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusReservaEstoque.PENDENTE;
        this.itensReservados = new ArrayList<>();
        atualizarItens(itensReservados);
    }

    public ReservaEstoque(String eventoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this(eventoId, dataInicio, dataFim, List.of(new ItemReserva("reserva-inicial", "item-inicial", 1)));
        this.itensReservados.clear();
    }

    public void atualizarSolicitacao(LocalDateTime novaDataInicio, LocalDateTime novaDataFim, List<ItemReserva> novosItens) {
        validar(this.eventoId, novaDataInicio, novaDataFim, novosItens);
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
        atualizarItens(novosItens);
    }

    public void confirmar() {
        if (this.status != StatusReservaEstoque.PENDENTE) {
            throw new IllegalStateException("Apenas reservas pendentes podem ser confirmadas.");
        }
        this.status = StatusReservaEstoque.CONFIRMADA;
    }

    public void iniciarUso() {
        if (this.status != StatusReservaEstoque.CONFIRMADA) {
            throw new IllegalStateException("Apenas reservas confirmadas podem entrar em uso.");
        }
        this.status = StatusReservaEstoque.EM_USO;
    }

    public void cancelar() {
        if (this.status == StatusReservaEstoque.FINALIZADA) {
            throw new IllegalStateException("Nao e possivel cancelar uma reserva ja finalizada.");
        }
        this.status = StatusReservaEstoque.CANCELADA;
    }

    public void finalizar() {
        if (this.status != StatusReservaEstoque.CONFIRMADA && this.status != StatusReservaEstoque.EM_USO) {
            throw new IllegalStateException("Apenas reservas confirmadas ou em uso podem ser finalizadas.");
        }
        this.status = StatusReservaEstoque.FINALIZADA;
    }

    public boolean sobrepoePeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return !this.dataInicio.isAfter(fim) && !inicio.isAfter(this.dataFim);
    }

    private void atualizarItens(List<ItemReserva> novosItens) {
        this.itensReservados.clear();
        for (ItemReserva item : novosItens) {
            this.itensReservados.add(new ItemReserva(this.id, item.getItemEstoqueId(), item.getQuantidade()));
        }
    }

    private void validar(String eventoId,
                         LocalDateTime dataInicio,
                         LocalDateTime dataFim,
                         List<ItemReserva> itensReservados) {
        if (eventoId == null || eventoId.isBlank()) {
            throw new IllegalArgumentException("ID do evento e obrigatorio.");
        }
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Periodo da reserva invalido.");
        }
        if (itensReservados == null || itensReservados.isEmpty()) {
            throw new IllegalArgumentException("A reserva deve possuir ao menos um item.");
        }
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public StatusReservaEstoque getStatus() {
        return status;
    }

    public List<ItemReserva> getItensReservados() {
        return Collections.unmodifiableList(itensReservados);
    }
}

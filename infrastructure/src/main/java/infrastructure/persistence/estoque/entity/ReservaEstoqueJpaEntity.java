package infrastructure.persistence.estoque.entity;

import domain.estoque.valueobject.StatusReservaEstoque;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reserva_estoque")
public class ReservaEstoqueJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusReservaEstoque status;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemReservaJpaEntity> itensReservados = new ArrayList<>();

    protected ReservaEstoqueJpaEntity() {
    }

    public ReservaEstoqueJpaEntity(String id, String eventoId, LocalDateTime dataInicio, LocalDateTime dataFim,
                                   StatusReservaEstoque status) {
        this.id = id;
        this.eventoId = eventoId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
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

    public List<ItemReservaJpaEntity> getItensReservados() {
        return itensReservados;
    }

    public void setItensReservados(List<ItemReservaJpaEntity> itensReservados) {
        this.itensReservados = itensReservados != null ? new ArrayList<>(itensReservados) : new ArrayList<>();
    }
}

package domain.local.turno.entity;

import domain.local.turno.valueobject.StatusTurno;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class TurnoOperacional {

    private final String id;
    private final String localId;
    private String nome;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private String diasDaSemana;
    private StatusTurno status;
    private Integer capacidade;
    private String observacoes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TurnoOperacional(String localId, String nome, LocalTime horaInicio, LocalTime horaFim,
                            String diasDaSemana, Integer capacidade, String observacoes) {
        validar(localId, nome, horaInicio, horaFim, diasDaSemana);
        this.id = UUID.randomUUID().toString();
        this.localId = localId;
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diasDaSemana = diasDaSemana;
        this.capacidade = capacidade;
        this.observacoes = observacoes;
        this.status = StatusTurno.ATIVO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    private TurnoOperacional(String id, String localId, String nome, LocalTime horaInicio, LocalTime horaFim,
                             String diasDaSemana, StatusTurno status, Integer capacidade, String observacoes,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.localId = localId;
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diasDaSemana = diasDaSemana;
        this.status = status;
        this.capacidade = capacidade;
        this.observacoes = observacoes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TurnoOperacional reconstituir(String id, String localId, String nome, LocalTime horaInicio,
                                                LocalTime horaFim, String diasDaSemana, StatusTurno status,
                                                Integer capacidade, String observacoes,
                                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new TurnoOperacional(id, localId, nome, horaInicio, horaFim, diasDaSemana, status,
                capacidade, observacoes, createdAt, updatedAt);
    }

    public void atualizar(String nome, LocalTime horaInicio, LocalTime horaFim, String diasDaSemana,
                          Integer capacidade, String observacoes) {
        validar(this.localId, nome, horaInicio, horaFim, diasDaSemana);
        this.nome = nome;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.diasDaSemana = diasDaSemana;
        this.capacidade = capacidade;
        this.observacoes = observacoes;
        this.updatedAt = LocalDateTime.now();
    }

    public void desativar() {
        this.status = StatusTurno.INATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    private void validar(String localId, String nome, LocalTime horaInicio, LocalTime horaFim, String diasDaSemana) {
        if (localId == null || localId.isBlank()) {
            throw new IllegalArgumentException("Local é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do turno é obrigatório.");
        }
        if (horaInicio == null || horaFim == null) {
            throw new IllegalArgumentException("Horário de início e fim são obrigatórios.");
        }
        if (!horaFim.isAfter(horaInicio)) {
            throw new IllegalArgumentException("Horário de fim deve ser posterior ao início.");
        }
        if (diasDaSemana == null || diasDaSemana.isBlank()) {
            throw new IllegalArgumentException("Ao menos um dia da semana é obrigatório.");
        }
    }

    public String getId() { return id; }
    public String getLocalId() { return localId; }
    public String getNome() { return nome; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public String getDiasDaSemana() { return diasDaSemana; }
    public StatusTurno getStatus() { return status; }
    public Integer getCapacidade() { return capacidade; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

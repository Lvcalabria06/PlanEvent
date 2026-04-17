package domain.tarefa.entity;

import domain.tarefa.valueobject.DependenciaTarefa;
import domain.tarefa.valueobject.StatusTarefa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Tarefa {
    private final String id;
    private final String equipeId;
    private String titulo;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusTarefa status;
    private final List<DependenciaTarefa> dependencias;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Tarefa() {
        this.id = UUID.randomUUID().toString();
        this.equipeId = null;
        this.dependencias = new ArrayList<>();
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public Tarefa(String equipeId, String titulo, String descricao, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (equipeId == null) {
            throw new IllegalArgumentException("ID da equipe é obrigatório.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de início não pode ser depois da data de fim.");
        }
        this.id = UUID.randomUUID().toString();
        this.equipeId = equipeId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusTarefa.PENDENTE;
        this.dependencias = new ArrayList<>();
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void iniciar() {
        if (this.status != StatusTarefa.PENDENTE) {
            throw new IllegalStateException("Apenas tarefas pendentes podem ser iniciadas.");
        }
        this.status = StatusTarefa.EM_ANDAMENTO;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void concluir() {
        if (this.status != StatusTarefa.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas tarefas em andamento podem ser concluídas.");
        }
        this.status = StatusTarefa.CONCLUIDA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusTarefa.CANCELADA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void adicionarDependencia(String tarefaId) {
        if (tarefaId == null || tarefaId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID da dependência é obrigatório.");
        }
        if (this.id.equals(tarefaId)) {
            throw new IllegalArgumentException("Uma tarefa não pode referenciar a si mesma como dependência.");
        }
        DependenciaTarefa novaDependencia = new DependenciaTarefa(tarefaId);
        if (!this.dependencias.contains(novaDependencia)) {
            this.dependencias.add(novaDependencia);
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void removerDependencia(String tarefaId) {
        if (tarefaId == null || tarefaId.trim().isEmpty()) {
            return;
        }
        boolean removido = this.dependencias.removeIf(d -> d.getTarefaPredecessoraId().equals(tarefaId));
        if (removido) {
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public List<String> listarDependencias() {
        return this.dependencias.stream()
                .map(DependenciaTarefa::getTarefaPredecessoraId)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    // Getters
    public String getId() { return id; }
    public String getEquipeId() { return equipeId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusTarefa getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}

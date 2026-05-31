package infrastructure.persistence.tarefa.entity;

import domain.tarefa.valueobject.StatusTarefa;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapeamento objeto-relacional da Tarefa. As dependências (ids de tarefas
 * predecessoras) são persistidas na tabela de junção {@code tarefa_dependencia}.
 */
@Entity
@Table(name = "tarefa")
public class TarefaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "equipe_id", nullable = false)
    private String equipeId;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusTarefa status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tarefa_dependencia",
            joinColumns = @JoinColumn(name = "tarefa_id"))
    @Column(name = "predecessora_id", nullable = false)
    private Set<String> dependenciasIds = new HashSet<>();

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected TarefaJpaEntity() {
    }

    public TarefaJpaEntity(String id, String equipeId, String titulo, String descricao,
            LocalDateTime dataInicio, LocalDateTime dataFim, StatusTarefa status,
            Set<String> dependenciasIds, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.equipeId = equipeId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.dependenciasIds = dependenciasIds != null ? new HashSet<>(dependenciasIds) : new HashSet<>();
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getId() {
        return id;
    }

    public String getEquipeId() {
        return equipeId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public Set<String> getDependenciasIds() {
        return dependenciasIds;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}

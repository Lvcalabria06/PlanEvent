package infrastructure.persistence.tarefa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Mapeamento objeto-relacional do vínculo entre uma tarefa e o funcionário
 * responsável por sua execução.
 */
@Entity
@Table(
        name = "responsavel_tarefa",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tarefa_id", "funcionario_id"}))
public class ResponsavelTarefaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "tarefa_id", nullable = false)
    private String tarefaId;

    @Column(name = "funcionario_id", nullable = false)
    private String funcionarioId;

    protected ResponsavelTarefaJpaEntity() {
    }

    public ResponsavelTarefaJpaEntity(String id, String tarefaId, String funcionarioId) {
        this.id = id;
        this.tarefaId = tarefaId;
        this.funcionarioId = funcionarioId;
    }

    public String getId() {
        return id;
    }

    public String getTarefaId() {
        return tarefaId;
    }

    public String getFuncionarioId() {
        return funcionarioId;
    }
}

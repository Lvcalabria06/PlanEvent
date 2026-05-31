package infrastructure.persistence.equipe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "membro_equipe")
public class MembroEquipeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "funcionario_id", nullable = false)
    private String funcionarioId;

    @Column(name = "lider", nullable = false)
    private boolean lider;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id", nullable = false)
    private EquipeJpaEntity equipe;

    protected MembroEquipeJpaEntity() {
    }

    public MembroEquipeJpaEntity(String id, String funcionarioId, boolean lider, LocalDateTime dataEntrada, EquipeJpaEntity equipe) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.lider = lider;
        this.dataEntrada = dataEntrada;
        this.equipe = equipe;
    }

    public String getId() {
        return id;
    }

    public String getFuncionarioId() {
        return funcionarioId;
    }

    public boolean isLider() {
        return lider;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public EquipeJpaEntity getEquipe() {
        return equipe;
    }
}

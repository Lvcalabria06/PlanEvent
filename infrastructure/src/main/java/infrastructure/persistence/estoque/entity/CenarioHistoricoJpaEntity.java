package infrastructure.persistence.estoque.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cenario_historico")
public class CenarioHistoricoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "usuario_responsavel_id", nullable = false)
    private String usuarioResponsavelId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cenario_id", nullable = false)
    private CenarioRedistribuicaoJpaEntity cenario;

    @OneToMany(mappedBy = "historico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AlocacaoRedistribuicaoJpaEntity> alocacoesSnapshot = new ArrayList<>();

    protected CenarioHistoricoJpaEntity() {
    }

    public CenarioHistoricoJpaEntity(String id, String usuarioResponsavelId, LocalDateTime dataHora,
                                     String descricao, CenarioRedistribuicaoJpaEntity cenario) {
        this.id = id;
        this.usuarioResponsavelId = usuarioResponsavelId;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.cenario = cenario;
    }

    public String getId() {
        return id;
    }

    public String getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public CenarioRedistribuicaoJpaEntity getCenario() {
        return cenario;
    }

    public List<AlocacaoRedistribuicaoJpaEntity> getAlocacoesSnapshot() {
        return alocacoesSnapshot;
    }

    public void setAlocacoesSnapshot(List<AlocacaoRedistribuicaoJpaEntity> alocacoesSnapshot) {
        this.alocacoesSnapshot = alocacoesSnapshot != null ? new ArrayList<>(alocacoesSnapshot) : new ArrayList<>();
    }
}

package infrastructure.persistence.estoque.entity;

import domain.estoque.valueobject.StatusRedistribuicao;
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
@Table(name = "cenario_redistribuicao")
public class CenarioRedistribuicaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "gerado_por_usuario_id", nullable = false)
    private String geradoPorUsuarioId;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDateTime periodoInicio;

    @Column(name = "periodo_fim", nullable = false)
    private LocalDateTime periodoFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRedistribuicao status;

    @Column(name = "data_aplicacao")
    private LocalDateTime dataAplicacao;

    @Column(name = "aplicado_por_usuario_id")
    private String aplicadoPorUsuarioId;

    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AlocacaoRedistribuicaoJpaEntity> alocacoes = new ArrayList<>();

    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CenarioHistoricoJpaEntity> historico = new ArrayList<>();

    protected CenarioRedistribuicaoJpaEntity() {
    }

    public CenarioRedistribuicaoJpaEntity(String id, LocalDateTime dataCriacao, String geradoPorUsuarioId,
                                          LocalDateTime periodoInicio, LocalDateTime periodoFim,
                                          StatusRedistribuicao status, LocalDateTime dataAplicacao,
                                          String aplicadoPorUsuarioId) {
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
        this.status = status;
        this.dataAplicacao = dataAplicacao;
        this.aplicadoPorUsuarioId = aplicadoPorUsuarioId;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public String getGeradoPorUsuarioId() {
        return geradoPorUsuarioId;
    }

    public LocalDateTime getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDateTime getPeriodoFim() {
        return periodoFim;
    }

    public StatusRedistribuicao getStatus() {
        return status;
    }

    public LocalDateTime getDataAplicacao() {
        return dataAplicacao;
    }

    public String getAplicadoPorUsuarioId() {
        return aplicadoPorUsuarioId;
    }

    public List<AlocacaoRedistribuicaoJpaEntity> getAlocacoes() {
        return alocacoes;
    }

    public void setAlocacoes(List<AlocacaoRedistribuicaoJpaEntity> alocacoes) {
        this.alocacoes = alocacoes != null ? new ArrayList<>(alocacoes) : new ArrayList<>();
    }

    public List<CenarioHistoricoJpaEntity> getHistorico() {
        return historico;
    }

    public void setHistorico(List<CenarioHistoricoJpaEntity> historico) {
        this.historico = historico != null ? new ArrayList<>(historico) : new ArrayList<>();
    }
}

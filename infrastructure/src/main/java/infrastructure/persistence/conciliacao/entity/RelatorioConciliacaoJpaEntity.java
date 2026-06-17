package infrastructure.persistence.conciliacao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relatorio_conciliacao")
public class RelatorioConciliacaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "responsavel_id", nullable = false)
    private String responsavelId;

    @Column(name = "data_geracao", nullable = false, updatable = false)
    private LocalDateTime dataGeracao;

    @OneToMany(mappedBy = "relatorio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRelatorioJpaEntity> itens = new ArrayList<>();

    protected RelatorioConciliacaoJpaEntity() {}

    public RelatorioConciliacaoJpaEntity(String id, String eventoId, String responsavelId,
            LocalDateTime dataGeracao) {
        this.id = id;
        this.eventoId = eventoId;
        this.responsavelId = responsavelId;
        this.dataGeracao = dataGeracao;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public List<ItemRelatorioJpaEntity> getItens() { return itens; }
}

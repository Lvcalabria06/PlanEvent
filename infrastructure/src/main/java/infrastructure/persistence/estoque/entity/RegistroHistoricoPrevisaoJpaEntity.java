package infrastructure.persistence.estoque.entity;

import domain.estoque.valueobject.TipoRegistroPrevisao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_historico_previsao")
public class RegistroHistoricoPrevisaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "versao", nullable = false)
    private int versao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false)
    private TipoRegistroPrevisao tipoRegistro;

    @Column(name = "usuario_responsavel_id", nullable = false)
    private String usuarioResponsavelId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "justificativa", columnDefinition = "TEXT")
    private String justificativa;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "item_previsao_historico",
            joinColumns = @JoinColumn(name = "registro_historico_id"))
    private List<ItemPrevisaoHistoricoJpaEntity> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previsao_id", nullable = false)
    private PrevisaoConsumoJpaEntity previsao;

    protected RegistroHistoricoPrevisaoJpaEntity() {
    }

    public RegistroHistoricoPrevisaoJpaEntity(String id, int versao, TipoRegistroPrevisao tipoRegistro,
                                                String usuarioResponsavelId, LocalDateTime dataHora,
                                                String justificativa, PrevisaoConsumoJpaEntity previsao) {
        this.id = id;
        this.versao = versao;
        this.tipoRegistro = tipoRegistro;
        this.usuarioResponsavelId = usuarioResponsavelId;
        this.dataHora = dataHora;
        this.justificativa = justificativa;
        this.previsao = previsao;
    }

    public String getId() {
        return id;
    }

    public int getVersao() {
        return versao;
    }

    public TipoRegistroPrevisao getTipoRegistro() {
        return tipoRegistro;
    }

    public String getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public List<ItemPrevisaoHistoricoJpaEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemPrevisaoHistoricoJpaEntity> itens) {
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
    }

    public PrevisaoConsumoJpaEntity getPrevisao() {
        return previsao;
    }
}

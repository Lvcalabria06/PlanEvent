package infrastructure.persistence.estoque.entity;

import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
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
@Table(name = "previsao_consumo")
public class PrevisaoConsumoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "gerado_por_usuario_id", nullable = false)
    private String geradoPorUsuarioId;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_historico", nullable = false)
    private StatusHistoricoPrevisao statusHistorico;

    @Column(name = "fallback_utilizado", nullable = false)
    private boolean fallbackUtilizado;

    @Column(name = "invalidada", nullable = false)
    private boolean invalidada;

    @Column(name = "versao_atual", nullable = false)
    private int versaoAtual;

    @Column(name = "total_eventos_base", nullable = false)
    private int totalEventosBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento_referencia")
    private TipoEvento tipoEventoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "porte_evento_referencia")
    private PorteEvento porteEventoReferencia;

    @Column(name = "duracao_horas_referencia", nullable = false)
    private long duracaoHorasReferencia;

    @OneToMany(mappedBy = "previsao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPrevisaoJpaEntity> itens = new ArrayList<>();

    @OneToMany(mappedBy = "previsao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RegistroHistoricoPrevisaoJpaEntity> historicoRegistros = new ArrayList<>();

    protected PrevisaoConsumoJpaEntity() {
    }

    public PrevisaoConsumoJpaEntity(String id, String eventoId, String geradoPorUsuarioId,
                                    LocalDateTime dataGeracao, StatusHistoricoPrevisao statusHistorico,
                                    boolean fallbackUtilizado, boolean invalidada, int versaoAtual,
                                    int totalEventosBase, TipoEvento tipoEventoReferencia,
                                    PorteEvento porteEventoReferencia, long duracaoHorasReferencia) {
        this.id = id;
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = dataGeracao;
        this.statusHistorico = statusHistorico;
        this.fallbackUtilizado = fallbackUtilizado;
        this.invalidada = invalidada;
        this.versaoAtual = versaoAtual;
        this.totalEventosBase = totalEventosBase;
        this.tipoEventoReferencia = tipoEventoReferencia;
        this.porteEventoReferencia = porteEventoReferencia;
        this.duracaoHorasReferencia = duracaoHorasReferencia;
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getGeradoPorUsuarioId() {
        return geradoPorUsuarioId;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public StatusHistoricoPrevisao getStatusHistorico() {
        return statusHistorico;
    }

    public boolean isFallbackUtilizado() {
        return fallbackUtilizado;
    }

    public boolean isInvalidada() {
        return invalidada;
    }

    public int getVersaoAtual() {
        return versaoAtual;
    }

    public int getTotalEventosBase() {
        return totalEventosBase;
    }

    public TipoEvento getTipoEventoReferencia() {
        return tipoEventoReferencia;
    }

    public PorteEvento getPorteEventoReferencia() {
        return porteEventoReferencia;
    }

    public long getDuracaoHorasReferencia() {
        return duracaoHorasReferencia;
    }

    public List<ItemPrevisaoJpaEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemPrevisaoJpaEntity> itens) {
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
    }

    public List<RegistroHistoricoPrevisaoJpaEntity> getHistoricoRegistros() {
        return historicoRegistros;
    }

    public void setHistoricoRegistros(List<RegistroHistoricoPrevisaoJpaEntity> historicoRegistros) {
        this.historicoRegistros = historicoRegistros != null ? new ArrayList<>(historicoRegistros) : new ArrayList<>();
    }
}

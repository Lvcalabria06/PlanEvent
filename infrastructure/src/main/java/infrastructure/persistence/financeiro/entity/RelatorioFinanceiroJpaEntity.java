package infrastructure.persistence.financeiro.entity;

import domain.financeiro.valueobject.TipoRelatorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio_financeiro")
public class RelatorioFinanceiroJpaEntity {

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
    @Column(name = "tipo", nullable = false)
    private TipoRelatorio tipo;

    @Column(name = "motivo_nova_versao_oficial")
    private String motivoNovaVersaoOficial;

    @Column(name = "total_geral_previsto", nullable = false)
    private BigDecimal totalGeralPrevisto;

    @Column(name = "total_geral_realizado", nullable = false)
    private BigDecimal totalGeralRealizado;

    @Column(name = "itens_por_categoria_json", columnDefinition = "TEXT")
    private String itensPorCategoriaJson;

    @Column(name = "saude_financeira_json", columnDefinition = "TEXT")
    private String saudeFinanceiraJson;

    @Column(name = "cobertura_contratual_json", columnDefinition = "TEXT")
    private String coberturaContratualJson;

    @Column(name = "comparativo_json", columnDefinition = "TEXT")
    private String comparativoJson;

    @Column(name = "recomendacoes_json", columnDefinition = "TEXT")
    private String recomendacoesJson;

    @Column(name = "conteudo", columnDefinition = "TEXT")
    private String conteudo;

    protected RelatorioFinanceiroJpaEntity() {
    }

    public RelatorioFinanceiroJpaEntity(String id, String eventoId, String geradoPorUsuarioId,
                                        LocalDateTime dataGeracao, TipoRelatorio tipo,
                                        String motivoNovaVersaoOficial, BigDecimal totalGeralPrevisto,
                                        BigDecimal totalGeralRealizado, String itensPorCategoriaJson,
                                        String saudeFinanceiraJson, String coberturaContratualJson,
                                        String comparativoJson, String recomendacoesJson, String conteudo) {
        this.id = id;
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = dataGeracao;
        this.tipo = tipo;
        this.motivoNovaVersaoOficial = motivoNovaVersaoOficial;
        this.totalGeralPrevisto = totalGeralPrevisto;
        this.totalGeralRealizado = totalGeralRealizado;
        this.itensPorCategoriaJson = itensPorCategoriaJson;
        this.saudeFinanceiraJson = saudeFinanceiraJson;
        this.coberturaContratualJson = coberturaContratualJson;
        this.comparativoJson = comparativoJson;
        this.recomendacoesJson = recomendacoesJson;
        this.conteudo = conteudo;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public TipoRelatorio getTipo() { return tipo; }
    public String getMotivoNovaVersaoOficial() { return motivoNovaVersaoOficial; }
    public BigDecimal getTotalGeralPrevisto() { return totalGeralPrevisto; }
    public BigDecimal getTotalGeralRealizado() { return totalGeralRealizado; }
    public String getItensPorCategoriaJson() { return itensPorCategoriaJson; }
    public String getSaudeFinanceiraJson() { return saudeFinanceiraJson; }
    public String getCoberturaContratualJson() { return coberturaContratualJson; }
    public String getComparativoJson() { return comparativoJson; }
    public String getRecomendacoesJson() { return recomendacoesJson; }
    public String getConteudo() { return conteudo; }
}

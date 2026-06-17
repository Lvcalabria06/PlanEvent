package domain.financeiro.entity;

import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.ResultadoGeracaoRelatorio;
import domain.financeiro.valueobject.SaudeFinanceira;
import domain.financeiro.valueobject.TipoRelatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RelatorioFinanceiro {

    private final String id;
    private final String eventoId;
    private final String geradoPorUsuarioId;
    private final LocalDateTime dataGeracao;
    private final TipoRelatorio tipo;
    private final String motivoNovaVersaoOficial;
    private final BigDecimal totalGeralPrevisto;
    private final BigDecimal totalGeralRealizado;
    private final List<ItemRelatorioCategoria> itensPorCategoria;
    private final SaudeFinanceira saudeFinanceira;
    private final IndicadorCoberturaContratual coberturaContratual;
    private final ComparativoRelatorioFinanceiro comparativo;
    private final List<RecomendacaoFinanceira> recomendacoes;
    private final String conteudo;

    public RelatorioFinanceiro(String eventoId,
                                String geradoPorUsuarioId,
                                BigDecimal totalGeralPrevisto,
                                BigDecimal totalGeralRealizado,
                                List<ItemRelatorioCategoria> itensPorCategoria,
                                String conteudo) {
        this(UUID.randomUUID().toString(),
                eventoId,
                geradoPorUsuarioId,
                LocalDateTime.now(),
                TipoRelatorio.PRELIMINAR,
                null,
                totalGeralPrevisto,
                totalGeralRealizado,
                itensPorCategoria,
                new SaudeFinanceira(itensPorCategoria),
                null,
                null,
                List.of(),
                conteudo);
    }

    public RelatorioFinanceiro(ResultadoGeracaoRelatorio resultado,
                                TipoRelatorio tipo,
                                String motivoNovaVersaoOficial) {
        this(UUID.randomUUID().toString(),
                resultado.getEventoId(),
                resultado.getUsuarioId(),
                LocalDateTime.now(),
                tipo,
                motivoNovaVersaoOficial,
                resultado.getTotalGeralPrevisto(),
                resultado.getTotalGeralRealizado(),
                resultado.getItens(),
                resultado.getSaudeFinanceira(),
                resultado.getCoberturaContratual(),
                resultado.getComparativo(),
                resultado.getRecomendacoes(),
                resultado.getConteudo());
    }

    public static RelatorioFinanceiro reconstruir(String id, String eventoId, String geradoPorUsuarioId,
                                                  LocalDateTime dataGeracao, TipoRelatorio tipo,
                                                  String motivoNovaVersaoOficial, BigDecimal totalGeralPrevisto,
                                                  BigDecimal totalGeralRealizado, List<ItemRelatorioCategoria> itensPorCategoria,
                                                  SaudeFinanceira saudeFinanceira, IndicadorCoberturaContratual coberturaContratual,
                                                  ComparativoRelatorioFinanceiro comparativo, List<RecomendacaoFinanceira> recomendacoes,
                                                  String conteudo) {
        return new RelatorioFinanceiro(id, eventoId, geradoPorUsuarioId, dataGeracao, tipo, motivoNovaVersaoOficial,
                totalGeralPrevisto, totalGeralRealizado, itensPorCategoria, saudeFinanceira, coberturaContratual, comparativo, recomendacoes, conteudo);
    }

    private RelatorioFinanceiro(String id,
                                 String eventoId,
                                 String geradoPorUsuarioId,
                                 LocalDateTime dataGeracao,
                                 TipoRelatorio tipo,
                                 String motivoNovaVersaoOficial,
                                 BigDecimal totalGeralPrevisto,
                                 BigDecimal totalGeralRealizado,
                                 List<ItemRelatorioCategoria> itensPorCategoria,
                                 SaudeFinanceira saudeFinanceira,
                                 IndicadorCoberturaContratual coberturaContratual,
                                 ComparativoRelatorioFinanceiro comparativo,
                                 List<RecomendacaoFinanceira> recomendacoes,
                                 String conteudo) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (geradoPorUsuarioId == null || geradoPorUsuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário responsável pela geração é obrigatório.");
        }
        if (itensPorCategoria == null || itensPorCategoria.isEmpty()) {
            throw new IllegalArgumentException(
                    "O relatório deve conter ao menos um item por categoria.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo do relatório é obrigatório.");
        }

        this.id = id;
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = dataGeracao;
        this.tipo = tipo;
        this.motivoNovaVersaoOficial = motivoNovaVersaoOficial;
        this.totalGeralPrevisto = totalGeralPrevisto != null ? totalGeralPrevisto : BigDecimal.ZERO;
        this.totalGeralRealizado = totalGeralRealizado != null ? totalGeralRealizado : BigDecimal.ZERO;
        this.itensPorCategoria = Collections.unmodifiableList(itensPorCategoria);
        this.saudeFinanceira = saudeFinanceira != null
                ? saudeFinanceira
                : new SaudeFinanceira(itensPorCategoria, coberturaContratual);
        this.coberturaContratual = coberturaContratual;
        this.comparativo = comparativo;
        this.recomendacoes = Collections.unmodifiableList(
                recomendacoes != null ? recomendacoes : List.of());
        this.conteudo = conteudo;
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

    public TipoRelatorio getTipo() {
        return tipo;
    }

    public String getMotivoNovaVersaoOficial() {
        return motivoNovaVersaoOficial;
    }

    public BigDecimal getTotalGeralPrevisto() {
        return totalGeralPrevisto;
    }

    public BigDecimal getTotalGeralRealizado() {
        return totalGeralRealizado;
    }

    public List<ItemRelatorioCategoria> getItensPorCategoria() {
        return itensPorCategoria;
    }

    public SaudeFinanceira getSaudeFinanceira() {
        return saudeFinanceira;
    }

    public IndicadorCoberturaContratual getCoberturaContratual() {
        return coberturaContratual;
    }

    public ComparativoRelatorioFinanceiro getComparativo() {
        return comparativo;
    }

    public List<RecomendacaoFinanceira> getRecomendacoes() {
        return recomendacoes;
    }

    public String getConteudo() {
        return conteudo;
    }
}

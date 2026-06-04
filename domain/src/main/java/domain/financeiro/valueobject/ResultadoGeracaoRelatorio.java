package domain.financeiro.valueobject;

import java.math.BigDecimal;
import java.util.List;

public class ResultadoGeracaoRelatorio {

    private final String eventoId;
    private final String usuarioId;
    private final BigDecimal totalGeralPrevisto;
    private final BigDecimal totalGeralRealizado;
    private final List<ItemRelatorioCategoria> itens;
    private final SaudeFinanceira saudeFinanceira;
    private final IndicadorCoberturaContratual coberturaContratual;
    private final ComparativoRelatorioFinanceiro comparativo;
    private final List<RecomendacaoFinanceira> recomendacoes;
    private final String conteudo;

    public ResultadoGeracaoRelatorio(String eventoId,
                                      String usuarioId,
                                      BigDecimal totalGeralPrevisto,
                                      BigDecimal totalGeralRealizado,
                                      List<ItemRelatorioCategoria> itens,
                                      SaudeFinanceira saudeFinanceira,
                                      IndicadorCoberturaContratual coberturaContratual,
                                      ComparativoRelatorioFinanceiro comparativo,
                                      List<RecomendacaoFinanceira> recomendacoes,
                                      String conteudo) {
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.totalGeralPrevisto = totalGeralPrevisto;
        this.totalGeralRealizado = totalGeralRealizado;
        this.itens = List.copyOf(itens);
        this.saudeFinanceira = saudeFinanceira;
        this.coberturaContratual = coberturaContratual;
        this.comparativo = comparativo;
        this.recomendacoes = List.copyOf(recomendacoes);
        this.conteudo = conteudo;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public BigDecimal getTotalGeralPrevisto() {
        return totalGeralPrevisto;
    }

    public BigDecimal getTotalGeralRealizado() {
        return totalGeralRealizado;
    }

    public List<ItemRelatorioCategoria> getItens() {
        return itens;
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

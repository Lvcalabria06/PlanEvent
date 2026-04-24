package domain.financeiro.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object que representa uma linha do relatório financeiro por categoria.
 *
 * RN2: Inclui total por categoria e percentual de variação entre previsto e realizado.
 * RN8: Percentual = ((realizado - previsto) / previsto) × 100.
 */
public class ItemRelatorioCategoria {

    private static final double LIMIAR_CRITICO = 20.0;

    private final CategoriaDespesa categoria;
    private final BigDecimal valorPrevisto;
    private final BigDecimal valorRealizado;
    private final double percentualVariacao;
    private final ClassificacaoDesvio classificacao;

    public ItemRelatorioCategoria(CategoriaDespesa categoria,
                                   BigDecimal valorPrevisto,
                                   BigDecimal valorRealizado) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }
        if (valorPrevisto == null || valorPrevisto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor previsto não pode ser nulo ou negativo.");
        }
        if (valorRealizado == null || valorRealizado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor realizado não pode ser nulo ou negativo.");
        }

        this.categoria = categoria;
        this.valorPrevisto = valorPrevisto;
        this.valorRealizado = valorRealizado;
        this.percentualVariacao = calcularPercentual(valorPrevisto, valorRealizado);
        this.classificacao = this.percentualVariacao > LIMIAR_CRITICO
                ? ClassificacaoDesvio.CRITICO
                : ClassificacaoDesvio.NORMAL;
    }

    /**
     * RN8: ((realizado - previsto) / previsto) × 100.
     * Se previsto == 0: 100% se houver realizado, 0% caso contrário.
     */
    private static double calcularPercentual(BigDecimal previsto, BigDecimal realizado) {
        if (previsto.compareTo(BigDecimal.ZERO) == 0) {
            return realizado.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return realizado.subtract(previsto)
                .divide(previsto, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public CategoriaDespesa getCategoria() { return categoria; }
    public BigDecimal getValorPrevisto() { return valorPrevisto; }
    public BigDecimal getValorRealizado() { return valorRealizado; }
    public double getPercentualVariacao() { return percentualVariacao; }
    public ClassificacaoDesvio getClassificacao() { return classificacao; }
    public boolean isCritico() { return classificacao == ClassificacaoDesvio.CRITICO; }
}

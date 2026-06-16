package domain.financeiro.valueobject;

import domain.financeiro.template.ClassificadorDesvioTemplateMethod;
import domain.financeiro.template.ClassificadorPorFaixasTemplateMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DesvioOrcamentario {

    private static final ClassificadorDesvioTemplateMethod CLASSIFICADOR_PADRAO =
            new ClassificadorPorFaixasTemplateMethod();

    private final CategoriaDespesa categoria;
    private final BigDecimal valorPrevisto;
    private final BigDecimal valorRealizado;
    private final double desvioPercentual;
    private final ClassificacaoDesvio classificacao;

    public DesvioOrcamentario(CategoriaDespesa categoria,
                               BigDecimal valorPrevisto,
                               BigDecimal valorRealizado) {
        this(categoria, valorPrevisto, valorRealizado, CLASSIFICADOR_PADRAO);
    }

    public DesvioOrcamentario(CategoriaDespesa categoria,
                               BigDecimal valorPrevisto,
                               BigDecimal valorRealizado,
                               ClassificadorDesvioTemplateMethod classificador) {

        if (categoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória.");
        }
        if (valorPrevisto == null || valorPrevisto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor previsto não pode ser nulo ou negativo.");
        }
        if (valorRealizado == null || valorRealizado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor realizado não pode ser nulo ou negativo.");
        }
        if (classificador == null) {
            throw new IllegalArgumentException("Classificador de desvio é obrigatório.");
        }

        this.categoria = categoria;
        this.valorPrevisto = valorPrevisto;
        this.valorRealizado = valorRealizado;
        this.desvioPercentual = calcularDesvioPercentual(valorPrevisto, valorRealizado);
        this.classificacao = classificador.classificar(desvioPercentual);
    }

    private static double calcularDesvioPercentual(BigDecimal previsto, BigDecimal realizado) {
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
    public double getDesvioPercentual() { return desvioPercentual; }
    public ClassificacaoDesvio getClassificacao() { return classificacao; }
    public boolean isCritico() { return classificacao == ClassificacaoDesvio.CRITICO; }
}

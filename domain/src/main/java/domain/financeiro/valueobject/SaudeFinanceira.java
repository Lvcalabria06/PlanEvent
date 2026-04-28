package domain.financeiro.valueobject;

import java.util.List;

public class SaudeFinanceira {

    private static final double LIMIAR_SAUDAVEL = 80.0;
    private static final double LIMIAR_ATENCAO  = 60.0;

    private final double score;
    private final ClassificacaoSaude classificacao;

    public SaudeFinanceira(List<ItemRelatorioCategoria> itens) {
        this.score = calcularScore(itens);
        this.classificacao = classificarScore(this.score);
    }

    private static double calcularScore(List<ItemRelatorioCategoria> itens) {
        if (itens == null || itens.isEmpty()) {
            return 100.0;
        }

        double totalPrevisto = itens.stream()
                .mapToDouble(i -> i.getValorPrevisto().doubleValue())
                .sum();

        if (totalPrevisto == 0) {
            return 100.0;
        }

        double score = 0.0;
        for (ItemRelatorioCategoria item : itens) {
            double peso = item.getValorPrevisto().doubleValue() / totalPrevisto;
            double desvio = item.getPercentualVariacao();

            double contribuicao;
            if (desvio <= 0) {
                contribuicao = peso * 100.0;
            } else {
                contribuicao = peso * Math.max(0.0, 100.0 - desvio);
            }
            score += contribuicao;
        }
        return score;
    }

    private static ClassificacaoSaude classificarScore(double score) {
        if (score >= LIMIAR_SAUDAVEL) return ClassificacaoSaude.SAUDAVEL;
        if (score >= LIMIAR_ATENCAO)  return ClassificacaoSaude.ATENCAO;
        return ClassificacaoSaude.CRITICO;
    }

    public double getScore() { return score; }
    public ClassificacaoSaude getClassificacao() { return classificacao; }
}

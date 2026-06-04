package domain.financeiro.valueobject;

public class IndicadorCoberturaContratual {

    private final int totalDespesasAtivas;
    private final int despesasCobertas;
    private final int despesasDescobertas;
    private final double percentualCobertura;

    public IndicadorCoberturaContratual(int totalDespesasAtivas,
                                         int despesasCobertas,
                                         int despesasDescobertas) {
        if (totalDespesasAtivas < 0 || despesasCobertas < 0 || despesasDescobertas < 0) {
            throw new IllegalArgumentException("Quantidades de cobertura não podem ser negativas.");
        }
        if (despesasCobertas + despesasDescobertas != totalDespesasAtivas) {
            throw new IllegalArgumentException(
                    "Cobertas e descobertas devem somar o total de despesas ativas.");
        }
        this.totalDespesasAtivas = totalDespesasAtivas;
        this.despesasCobertas = despesasCobertas;
        this.despesasDescobertas = despesasDescobertas;
        this.percentualCobertura = totalDespesasAtivas == 0
                ? 100.0
                : (despesasCobertas * 100.0) / totalDespesasAtivas;
    }

    public int getTotalDespesasAtivas() {
        return totalDespesasAtivas;
    }

    public int getDespesasCobertas() {
        return despesasCobertas;
    }

    public int getDespesasDescobertas() {
        return despesasDescobertas;
    }

    public double getPercentualCobertura() {
        return percentualCobertura;
    }

    public boolean possuiDespesasDescobertas() {
        return despesasDescobertas > 0;
    }
}

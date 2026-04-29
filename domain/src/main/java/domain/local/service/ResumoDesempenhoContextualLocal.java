package domain.local.service;

public class ResumoDesempenhoContextualLocal {
    private final double notaMediaGeral;
    private final double notaMediaContexto;
    private final int totalAvaliacoesLocal;
    private final int totalAvaliacoesContexto;
    private final boolean baixaBaseHistoricaContexto;
    private final String classificacaoGeral;
    private final String classificacaoContextual;

    public ResumoDesempenhoContextualLocal(
            double notaMediaGeral,
            double notaMediaContexto,
            int totalAvaliacoesLocal,
            int totalAvaliacoesContexto,
            boolean baixaBaseHistoricaContexto,
            String classificacaoGeral,
            String classificacaoContextual) {
        this.notaMediaGeral = notaMediaGeral;
        this.notaMediaContexto = notaMediaContexto;
        this.totalAvaliacoesLocal = totalAvaliacoesLocal;
        this.totalAvaliacoesContexto = totalAvaliacoesContexto;
        this.baixaBaseHistoricaContexto = baixaBaseHistoricaContexto;
        this.classificacaoGeral = classificacaoGeral;
        this.classificacaoContextual = classificacaoContextual;
    }

    public double getNotaMediaGeral() {
        return notaMediaGeral;
    }

    public double getNotaMediaContexto() {
        return notaMediaContexto;
    }

    public int getTotalAvaliacoesLocal() {
        return totalAvaliacoesLocal;
    }

    public int getTotalAvaliacoesContexto() {
        return totalAvaliacoesContexto;
    }

    public boolean isBaixaBaseHistoricaContexto() {
        return baixaBaseHistoricaContexto;
    }

    public String getClassificacaoGeral() {
        return classificacaoGeral;
    }

    public String getClassificacaoContextual() {
        return classificacaoContextual;
    }
}

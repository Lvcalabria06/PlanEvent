package presentationbackend.controller.local;

import domain.local.service.ResumoDesempenhoContextualLocal;

public record ResumoDesempenhoResponse(
        double notaMediaGeral,
        double notaMediaContexto,
        int totalAvaliacoesLocal,
        int totalAvaliacoesContexto,
        boolean baixaBaseHistoricaContexto,
        String classificacaoGeral,
        String classificacaoContextual
) {
    public static ResumoDesempenhoResponse de(ResumoDesempenhoContextualLocal resumo) {
        return new ResumoDesempenhoResponse(
                resumo.getNotaMediaGeral(),
                resumo.getNotaMediaContexto(),
                resumo.getTotalAvaliacoesLocal(),
                resumo.getTotalAvaliacoesContexto(),
                resumo.isBaixaBaseHistoricaContexto(),
                resumo.getClassificacaoGeral(),
                resumo.getClassificacaoContextual()
        );
    }
}

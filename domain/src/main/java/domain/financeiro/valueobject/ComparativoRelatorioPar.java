package domain.financeiro.valueobject;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resultado da comparação entre dois snapshots escolhidos pelo usuário (RN17 da Funcionalidade 2).
 * Somente leitura — não altera nenhum dos relatórios envolvidos.
 */
public class ComparativoRelatorioPar {

    private final String relatorioBaseId;
    private final String relatorioComparadoId;
    private final double variacaoScore;
    private final BigDecimal variacaoTotalRealizado;
    private final TendenciaSaudeFinanceira tendencia;
    private final List<CategoriaDespesa> categoriasComPiora;
    private final List<CategoriaDespesa> categoriasComMelhora;

    private ComparativoRelatorioPar(String relatorioBaseId,
                                     String relatorioComparadoId,
                                     double variacaoScore,
                                     BigDecimal variacaoTotalRealizado,
                                     TendenciaSaudeFinanceira tendencia,
                                     List<CategoriaDespesa> categoriasComPiora,
                                     List<CategoriaDespesa> categoriasComMelhora) {
        this.relatorioBaseId = relatorioBaseId;
        this.relatorioComparadoId = relatorioComparadoId;
        this.variacaoScore = variacaoScore;
        this.variacaoTotalRealizado = variacaoTotalRealizado;
        this.tendencia = tendencia;
        this.categoriasComPiora = List.copyOf(categoriasComPiora);
        this.categoriasComMelhora = List.copyOf(categoriasComMelhora);
    }

    public static ComparativoRelatorioPar comparar(RelatorioFinanceiro base,
                                                    RelatorioFinanceiro comparado) {
        if (base == null || comparado == null) {
            throw new IllegalArgumentException("Ambos os relatórios são obrigatórios.");
        }
        if (!base.getEventoId().equals(comparado.getEventoId())) {
            throw new IllegalArgumentException(
                    "Os relatórios devem pertencer ao mesmo evento.");
        }

        double variacaoScore = comparado.getSaudeFinanceira().getScore()
                - base.getSaudeFinanceira().getScore();
        BigDecimal variacaoRealizado = comparado.getTotalGeralRealizado()
                .subtract(base.getTotalGeralRealizado());
        TendenciaSaudeFinanceira tendencia = classificarTendencia(variacaoScore);

        Map<CategoriaDespesa, ItemRelatorioCategoria> mapaBase = base.getItensPorCategoria()
                .stream()
                .collect(Collectors.toMap(ItemRelatorioCategoria::getCategoria, Function.identity()));

        List<CategoriaDespesa> pioras = new ArrayList<>();
        List<CategoriaDespesa> melhoras = new ArrayList<>();

        for (ItemRelatorioCategoria itemComparado : comparado.getItensPorCategoria()) {
            ItemRelatorioCategoria itemBase = mapaBase.get(itemComparado.getCategoria());
            if (itemBase == null) continue;
            int nivelBase = nivelDesvio(itemBase);
            int nivelComparado = nivelDesvio(itemComparado);
            if (nivelComparado > nivelBase) {
                pioras.add(itemComparado.getCategoria());
            } else if (nivelComparado < nivelBase) {
                melhoras.add(itemComparado.getCategoria());
            }
        }

        return new ComparativoRelatorioPar(
                base.getId(), comparado.getId(),
                variacaoScore, variacaoRealizado,
                tendencia, pioras, melhoras);
    }

    private static TendenciaSaudeFinanceira classificarTendencia(double variacao) {
        if (variacao >= 5.0) return TendenciaSaudeFinanceira.MELHOROU;
        if (variacao <= -5.0) return TendenciaSaudeFinanceira.PIOROU;
        return TendenciaSaudeFinanceira.ESTAVEL;
    }

    private static int nivelDesvio(ItemRelatorioCategoria item) {
        return switch (item.getClassificacao()) {
            case NORMAL -> 0;
            case ATENCAO -> 1;
            case CRITICO -> 2;
        };
    }

    public String getRelatorioBaseId() { return relatorioBaseId; }
    public String getRelatorioComparadoId() { return relatorioComparadoId; }
    public double getVariacaoScore() { return variacaoScore; }
    public BigDecimal getVariacaoTotalRealizado() { return variacaoTotalRealizado; }
    public TendenciaSaudeFinanceira getTendencia() { return tendencia; }
    public List<CategoriaDespesa> getCategoriasComPiora() { return categoriasComPiora; }
    public List<CategoriaDespesa> getCategoriasComMelhora() { return categoriasComMelhora; }
}

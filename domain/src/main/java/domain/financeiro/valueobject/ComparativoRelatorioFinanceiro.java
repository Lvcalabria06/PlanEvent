package domain.financeiro.valueobject;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComparativoRelatorioFinanceiro {

    private static final double LIMIAR_VARIACAO_SCORE = 5.0;

    private final String relatorioAnteriorId;
    private final double variacaoScore;
    private final TendenciaSaudeFinanceira tendencia;
    private final List<CategoriaDespesa> categoriasComPiora;

    public ComparativoRelatorioFinanceiro(String relatorioAnteriorId,
                                           double variacaoScore,
                                           TendenciaSaudeFinanceira tendencia,
                                           List<CategoriaDespesa> categoriasComPiora) {
        if (relatorioAnteriorId == null || relatorioAnteriorId.isBlank()) {
            throw new IllegalArgumentException("ID do relatório anterior é obrigatório.");
        }
        if (tendencia == null) {
            throw new IllegalArgumentException("Tendência é obrigatória.");
        }
        this.relatorioAnteriorId = relatorioAnteriorId;
        this.variacaoScore = variacaoScore;
        this.tendencia = tendencia;
        this.categoriasComPiora = List.copyOf(categoriasComPiora);
    }

    public static ComparativoRelatorioFinanceiro calcular(RelatorioFinanceiro anterior,
                                                           List<ItemRelatorioCategoria> itensAtuais,
                                                           SaudeFinanceira saudeAtual) {
        double variacao = saudeAtual.getScore() - anterior.getSaudeFinanceira().getScore();
        TendenciaSaudeFinanceira tendencia = classificarTendencia(variacao);
        List<CategoriaDespesa> pioras = identificarCategoriasComPiora(
                anterior.getItensPorCategoria(), itensAtuais);
        return new ComparativoRelatorioFinanceiro(
                anterior.getId(), variacao, tendencia, pioras);
    }

    private static TendenciaSaudeFinanceira classificarTendencia(double variacao) {
        if (variacao >= LIMIAR_VARIACAO_SCORE) {
            return TendenciaSaudeFinanceira.MELHOROU;
        }
        if (variacao <= -LIMIAR_VARIACAO_SCORE) {
            return TendenciaSaudeFinanceira.PIOROU;
        }
        return TendenciaSaudeFinanceira.ESTAVEL;
    }

    private static List<CategoriaDespesa> identificarCategoriasComPiora(
            List<ItemRelatorioCategoria> itensAnteriores,
            List<ItemRelatorioCategoria> itensAtuais) {

        Map<CategoriaDespesa, ItemRelatorioCategoria> mapaAnterior = itensAnteriores.stream()
                .collect(Collectors.toMap(ItemRelatorioCategoria::getCategoria, Function.identity()));

        List<CategoriaDespesa> pioras = new ArrayList<>();
        for (ItemRelatorioCategoria atual : itensAtuais) {
            ItemRelatorioCategoria ant = mapaAnterior.get(atual.getCategoria());
            if (ant != null && nivelDesvio(atual) > nivelDesvio(ant)) {
                pioras.add(atual.getCategoria());
            }
        }
        return pioras;
    }

    private static int nivelDesvio(ItemRelatorioCategoria item) {
        return switch (item.getClassificacao()) {
            case NORMAL -> 0;
            case ATENCAO -> 1;
            case CRITICO -> 2;
        };
    }

    public String getRelatorioAnteriorId() {
        return relatorioAnteriorId;
    }

    public double getVariacaoScore() {
        return variacaoScore;
    }

    public TendenciaSaudeFinanceira getTendencia() {
        return tendencia;
    }

    public List<CategoriaDespesa> getCategoriasComPiora() {
        return categoriasComPiora;
    }
}

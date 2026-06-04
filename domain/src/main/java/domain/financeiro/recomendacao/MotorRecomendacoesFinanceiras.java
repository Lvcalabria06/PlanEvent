package domain.financeiro.recomendacao;

import domain.financeiro.valueobject.ClassificacaoDesvio;
import domain.financeiro.valueobject.ClassificacaoSaude;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.SaudeFinanceira;
import domain.financeiro.valueobject.TendenciaSaudeFinanceira;
import domain.financeiro.valueobject.TipoRecomendacaoFinanceira;

import java.util.ArrayList;
import java.util.List;

public final class MotorRecomendacoesFinanceiras {

    private MotorRecomendacoesFinanceiras() {
    }

    public static List<RecomendacaoFinanceira> gerar(List<ItemRelatorioCategoria> itens,
                                                      SaudeFinanceira saude,
                                                      ComparativoRelatorioFinanceiro comparativo,
                                                      IndicadorCoberturaContratual cobertura) {
        List<RecomendacaoFinanceira> recomendacoes = new ArrayList<>();

        if (saude.getClassificacao() == ClassificacaoSaude.CRITICO) {
            recomendacoes.add(new RecomendacaoFinanceira(
                    TipoRecomendacaoFinanceira.SAUDE_CRITICA,
                    "Saúde financeira CRÍTICA: revisar gastos e priorizar contenção de custos no evento."));
        }

        for (ItemRelatorioCategoria item : itens) {
            if (item.getClassificacao() == ClassificacaoDesvio.CRITICO) {
                recomendacoes.add(new RecomendacaoFinanceira(
                        TipoRecomendacaoFinanceira.CATEGORIA_CRITICA,
                        "Categoria " + item.getCategoria()
                                + " com desvio crítico (" + String.format("%.1f", item.getPercentualVariacao())
                                + "%): renegociar fornecedores ou revisar escopo.",
                        item.getCategoria()));
            }
        }

        if (comparativo != null && comparativo.getTendencia() == TendenciaSaudeFinanceira.PIOROU) {
            recomendacoes.add(new RecomendacaoFinanceira(
                    TipoRecomendacaoFinanceira.EVOLUCAO_PIOROU,
                    "Situação piorou em relação ao relatório anterior (variação de score: "
                            + String.format("%.1f", comparativo.getVariacaoScore()) + " pontos)."));
        }

        if (cobertura != null && cobertura.possuiDespesasDescobertas()) {
            recomendacoes.add(new RecomendacaoFinanceira(
                    TipoRecomendacaoFinanceira.COBERTURA_CONTRATUAL,
                    cobertura.getDespesasDescobertas() + " despesa(s) ativa(s) sem cobertura contratual. "
                            + "Executar conciliação ou formalizar contratos antes de nova emissão oficial."));
        }

        if (saude.getClassificacao() == ClassificacaoSaude.ATENCAO
                && recomendacoes.stream().noneMatch(r -> r.getTipo() == TipoRecomendacaoFinanceira.SAUDE_CRITICA)) {
            recomendacoes.add(new RecomendacaoFinanceira(
                    TipoRecomendacaoFinanceira.REVISAR_ORCAMENTO,
                    "Saúde financeira em ATENÇÃO: monitorar categorias com desvio positivo nas próximas semanas."));
        }

        return List.copyOf(recomendacoes);
    }
}

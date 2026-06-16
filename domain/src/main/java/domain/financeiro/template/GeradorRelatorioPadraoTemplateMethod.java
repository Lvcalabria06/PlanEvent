package domain.financeiro.template;

import domain.conciliacao.service.ConciliacaoService;
import domain.evento.repository.EventoRepository;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.SaudeFinanceira;

import java.math.BigDecimal;
import java.util.List;

public class GeradorRelatorioPadraoTemplateMethod extends GeradorRelatorioFinanceiroTemplateMethod {

    public GeradorRelatorioPadraoTemplateMethod(
            RelatorioFinanceiroRepository relatorioRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository,
            ConciliacaoService conciliacaoService) {
        super(relatorioRepository, orcamentoEventoRepository, categoriaOrcamentoRepository,
                despesaRepository, eventoRepository, conciliacaoService);
    }

    @Override
    protected String construirConteudo(String eventoId,
                                        BigDecimal totalPrevisto,
                                        BigDecimal totalRealizado,
                                        List<ItemRelatorioCategoria> itens,
                                        SaudeFinanceira saude,
                                        IndicadorCoberturaContratual cobertura,
                                        ComparativoRelatorioFinanceiro comparativo,
                                        List<RecomendacaoFinanceira> recomendacoes) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO FINANCEIRO — Evento: ").append(eventoId).append("\n");
        sb.append("Total Previsto: ").append(totalPrevisto).append("\n");
        sb.append("Total Realizado: ").append(totalRealizado).append("\n");
        sb.append(String.format("Score Saúde: %.2f (%s)%n",
                saude.getScore(), saude.getClassificacao()));
        sb.append(String.format("Cobertura contratual: %.1f%% (%d cobertas, %d descobertas)%n",
                cobertura.getPercentualCobertura(),
                cobertura.getDespesasCobertas(),
                cobertura.getDespesasDescobertas()));
        if (comparativo != null) {
            sb.append(String.format("Comparativo: %s (Δ score %.2f)%n",
                    comparativo.getTendencia(), comparativo.getVariacaoScore()));
        }
        sb.append("\nPor Categoria:\n");
        for (ItemRelatorioCategoria item : itens) {
            sb.append(String.format("  %s | Previsto: %s | Realizado: %s | Variação: %.2f%% | %s%n",
                    item.getCategoria(),
                    item.getValorPrevisto(),
                    item.getValorRealizado(),
                    item.getPercentualVariacao(),
                    item.getClassificacao()));
        }
        if (!recomendacoes.isEmpty()) {
            sb.append("\nRecomendações:\n");
            for (RecomendacaoFinanceira rec : recomendacoes) {
                sb.append("  - ").append(rec.getMensagem()).append("\n");
            }
        }
        return sb.toString();
    }
}

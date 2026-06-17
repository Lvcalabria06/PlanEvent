package domain.financeiro.template;

import domain.conciliacao.service.ConciliacaoService;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.ParametrosCenarioSimulacao;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.SaudeFinanceira;
import domain.financeiro.valueobject.StatusDespesa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Gerador de relatório com suporte a parâmetros de cenário what-if (RN15).
 * Sobrescreve os hooks protegidos para ajustar totais sem tocar no ledger real.
 */
public class GeradorRelatorioWhatIfTemplateMethod extends GeradorRelatorioFinanceiroTemplateMethod {

    private final ParametrosCenarioSimulacao parametros;

    public GeradorRelatorioWhatIfTemplateMethod(
            RelatorioFinanceiroRepository relatorioRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository,
            ConciliacaoService conciliacaoService,
            ParametrosCenarioSimulacao parametros) {
        super(relatorioRepository, orcamentoEventoRepository, categoriaOrcamentoRepository,
                despesaRepository, eventoRepository, conciliacaoService);
        this.parametros = parametros != null ? parametros : ParametrosCenarioSimulacao.padrao();
    }

    @Override
    protected List<ItemRelatorioCategoria> montarItens(String eventoId,
                                                        List<CategoriaOrcamento> categorias) {
        Map<CategoriaDespesa, BigDecimal> totais = new EnumMap<>(CategoriaDespesa.class);

        for (Despesa d : despesaRepository.listarPorEventoId(eventoId)) {
            boolean incluir = switch (d.getStatus()) {
                case REGISTRADA, APROVADA -> true;
                case PENDENTE_APROVACAO -> parametros.isIncluirPendentes();
                case REJEITADA -> false;
            };
            if (incluir) {
                totais.merge(d.getCategoria(), d.getValor(), BigDecimal::add);
            }
        }

        for (ParametrosCenarioSimulacao.DespesaHipotetica hip : parametros.getDespesasHipoteticas()) {
            totais.merge(hip.categoria(), hip.valor(), BigDecimal::add);
        }

        List<ItemRelatorioCategoria> itens = new ArrayList<>();
        for (CategoriaOrcamento cat : categorias) {
            BigDecimal realizado = totais.getOrDefault(cat.getNome(), BigDecimal.ZERO);
            itens.add(new ItemRelatorioCategoria(cat.getNome(), cat.getValorPrevisto(), realizado));
        }
        return itens;
    }

    @Override
    protected IndicadorCoberturaContratual montarIndicadorCobertura(String eventoId) {
        List<Despesa> ativas = despesaRepository.listarPorEventoId(eventoId).stream()
                .filter(d -> d.getStatus() != StatusDespesa.REJEITADA)
                .toList();
        int total = ativas.size();

        if (parametros.isCenarioPessimistaCobertura()) {
            return new IndicadorCoberturaContratual(total, 0, total);
        }

        int descobertas = conciliacaoService.listarDespesasDescobertasPorEvento(eventoId).size();
        int cobertas = Math.max(0, total - descobertas);
        return new IndicadorCoberturaContratual(total, cobertas, descobertas);
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
        sb.append("[SIMULAÇÃO WHAT-IF] RELATÓRIO FINANCEIRO — Evento: ").append(eventoId).append("\n");
        sb.append("Parâmetros: incluirPendentes=").append(parametros.isIncluirPendentes());
        sb.append(", cenarioPessimista=").append(parametros.isCenarioPessimistaCobertura());
        sb.append(", hipoteticas=").append(parametros.getDespesasHipoteticas().size()).append("\n");
        sb.append("Total Previsto: ").append(totalPrevisto).append("\n");
        sb.append("Total Realizado: ").append(totalRealizado).append("\n");
        sb.append(String.format("Score Saúde: %.2f (%s)%n", saude.getScore(), saude.getClassificacao()));
        sb.append("\nPor Categoria:\n");
        for (ItemRelatorioCategoria item : itens) {
            sb.append(String.format("  %s | Previsto: %s | Realizado: %s | Variação: %.2f%% | %s%n",
                    item.getCategoria(), item.getValorPrevisto(), item.getValorRealizado(),
                    item.getPercentualVariacao(), item.getClassificacao()));
        }
        return sb.toString();
    }
}

package domain.financeiro.template;

import domain.conciliacao.service.ConciliacaoService;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.recomendacao.MotorRecomendacoesFinanceiras;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.ResultadoGeracaoRelatorio;
import domain.financeiro.valueobject.SaudeFinanceira;
import domain.financeiro.valueobject.StatusDespesa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class GeradorRelatorioFinanceiroTemplateMethod {

    protected final RelatorioFinanceiroRepository relatorioRepository;
    protected final OrcamentoEventoRepository orcamentoEventoRepository;
    protected final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    protected final DespesaRepository despesaRepository;
    protected final EventoRepository eventoRepository;
    protected final ConciliacaoService conciliacaoService;

    protected GeradorRelatorioFinanceiroTemplateMethod(
            RelatorioFinanceiroRepository relatorioRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository,
            ConciliacaoService conciliacaoService) {
        this.relatorioRepository = relatorioRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
        this.despesaRepository = despesaRepository;
        this.eventoRepository = eventoRepository;
        this.conciliacaoService = conciliacaoService;
    }

    public final ResultadoGeracaoRelatorio executar(String eventoId, String usuarioId) {
        Evento evento = validarEvento(eventoId);
        antesDeMontarDados(evento);
        OrcamentoEvento orcamento = obterOrcamento(eventoId);
        List<CategoriaOrcamento> categorias = obterCategorias(orcamento);
        List<ItemRelatorioCategoria> itens = montarItens(eventoId, categorias);
        BigDecimal totalPrevisto = somarPrevisto(itens);
        BigDecimal totalRealizado = somarRealizado(itens);
        IndicadorCoberturaContratual cobertura = montarIndicadorCobertura(eventoId);
        SaudeFinanceira saude = new SaudeFinanceira(itens, cobertura);
        ComparativoRelatorioFinanceiro comparativo = montarComparativo(eventoId, itens, saude);
        var recomendacoes = MotorRecomendacoesFinanceiras.gerar(itens, saude, comparativo, cobertura);
        String conteudo = construirConteudo(eventoId, totalPrevisto, totalRealizado, itens, saude,
                cobertura, comparativo, recomendacoes);
        return new ResultadoGeracaoRelatorio(
                eventoId, usuarioId, totalPrevisto, totalRealizado, itens, saude,
                cobertura, comparativo, recomendacoes, conteudo);
    }

    protected void antesDeMontarDados(Evento evento) {
        // hook para validações de tipo oficial etc.
    }

    protected Evento validarEvento(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));
    }

    protected OrcamentoEvento obterOrcamento(String eventoId) {
        return orcamentoEventoRepository.buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. "
                                + "Cadastre o orçamento previsto por categoria antes de gerar o relatório."));
    }

    protected List<CategoriaOrcamento> obterCategorias(OrcamentoEvento orcamento) {
        List<CategoriaOrcamento> categorias = categoriaOrcamentoRepository
                .listarPorOrcamentoId(orcamento.getId());
        if (categorias == null || categorias.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível gerar o relatório pois não há orçamento "
                            + "previsto cadastrado por categoria para este evento.");
        }
        return categorias;
    }

    protected List<ItemRelatorioCategoria> montarItens(String eventoId,
                                                        List<CategoriaOrcamento> categorias) {
        List<ItemRelatorioCategoria> itens = new ArrayList<>();
        for (CategoriaOrcamento cat : categorias) {
            BigDecimal realizado = despesaRepository
                    .somarValoresAtivosPorEventoECategoria(eventoId, cat.getNome());
            BigDecimal realizadoFinal = realizado != null ? realizado : BigDecimal.ZERO;
            itens.add(new ItemRelatorioCategoria(
                    cat.getNome(), cat.getValorPrevisto(), realizadoFinal));
        }
        return itens;
    }

    protected IndicadorCoberturaContratual montarIndicadorCobertura(String eventoId) {
        List<Despesa> ativas = despesaRepository.listarPorEventoId(eventoId).stream()
                .filter(d -> d.getStatus() != StatusDespesa.REJEITADA)
                .toList();
        int descobertas = conciliacaoService.listarDespesasDescobertasPorEvento(eventoId).size();
        int total = ativas.size();
        int cobertas = Math.max(0, total - descobertas);
        return new IndicadorCoberturaContratual(total, cobertas, descobertas);
    }

    protected ComparativoRelatorioFinanceiro montarComparativo(String eventoId,
                                                                List<ItemRelatorioCategoria> itens,
                                                                SaudeFinanceira saude) {
        Optional<RelatorioFinanceiro> anterior = relatorioRepository.listarPorEventoId(eventoId).stream()
                .max(Comparator.comparing(RelatorioFinanceiro::getDataGeracao));
        return anterior
                .map(ant -> ComparativoRelatorioFinanceiro.calcular(ant, itens, saude))
                .orElse(null);
    }

    protected abstract String construirConteudo(String eventoId,
                                                 BigDecimal totalPrevisto,
                                                 BigDecimal totalRealizado,
                                                 List<ItemRelatorioCategoria> itens,
                                                 SaudeFinanceira saude,
                                                 IndicadorCoberturaContratual cobertura,
                                                 ComparativoRelatorioFinanceiro comparativo,
                                                 List<domain.financeiro.valueobject.RecomendacaoFinanceira> recomendacoes);

    private static BigDecimal somarPrevisto(List<ItemRelatorioCategoria> itens) {
        return itens.stream()
                .map(ItemRelatorioCategoria::getValorPrevisto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal somarRealizado(List<ItemRelatorioCategoria> itens) {
        return itens.stream()
                .map(ItemRelatorioCategoria::getValorRealizado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

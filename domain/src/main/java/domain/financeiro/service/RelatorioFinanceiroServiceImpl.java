package domain.financeiro.service;

import domain.evento.repository.EventoRepository;
import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.valueobject.ItemRelatorioCategoria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class RelatorioFinanceiroServiceImpl implements RelatorioFinanceiroService {

    private final RelatorioFinanceiroRepository relatorioRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;
    private final DespesaRepository despesaRepository;
    private final EventoRepository eventoRepository;

    public RelatorioFinanceiroServiceImpl(
            RelatorioFinanceiroRepository relatorioRepository,
            OrcamentoEventoRepository orcamentoEventoRepository,
            CategoriaOrcamentoRepository categoriaOrcamentoRepository,
            DespesaRepository despesaRepository,
            EventoRepository eventoRepository) {
        this.relatorioRepository = relatorioRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
        this.despesaRepository = despesaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId) {


        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));

   
        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. " +
                        "Cadastre o orçamento previsto por categoria antes de gerar o relatório."));


        List<CategoriaOrcamento> categorias = categoriaOrcamentoRepository
                .listarPorOrcamentoId(orcamento.getId());

        if (categorias == null || categorias.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível gerar o relatório pois não há orçamento " +
                    "previsto cadastrado por categoria para este evento.");
        }

        List<ItemRelatorioCategoria> itens = new ArrayList<>();
        BigDecimal totalGeralPrevisto = BigDecimal.ZERO;
        BigDecimal totalGeralRealizado = BigDecimal.ZERO;

        for (CategoriaOrcamento cat : categorias) {

            BigDecimal realizado = despesaRepository
                    .somarValoresPorEventoECategoria(eventoId, cat.getNome());

            BigDecimal realizadoFinal = realizado != null ? realizado : BigDecimal.ZERO;

            itens.add(new ItemRelatorioCategoria(
                    cat.getNome(),
                    cat.getValorPrevisto(),
                    realizadoFinal));

            totalGeralPrevisto = totalGeralPrevisto.add(cat.getValorPrevisto());
            totalGeralRealizado = totalGeralRealizado.add(realizadoFinal);
        }

        String conteudo = construirConteudo(eventoId, totalGeralPrevisto, totalGeralRealizado, itens);

        RelatorioFinanceiro relatorio = new RelatorioFinanceiro(
                eventoId,
                usuarioId,
                totalGeralPrevisto,
                totalGeralRealizado,
                itens,
                conteudo);

        return relatorioRepository.salvar(relatorio);
    }

    @Override
    public RelatorioFinanceiro buscarRelatorio(String id) {
        return relatorioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Relatório financeiro não encontrado."));
    }

    @Override
    public List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId) {
        return relatorioRepository.listarPorEventoId(eventoId);
    }

    private String construirConteudo(String eventoId,
                                      BigDecimal totalPrevisto,
                                      BigDecimal totalRealizado,
                                      List<ItemRelatorioCategoria> itens) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO FINANCEIRO — Evento: ").append(eventoId).append("\n");
        sb.append("Total Previsto: ").append(totalPrevisto).append("\n");
        sb.append("Total Realizado: ").append(totalRealizado).append("\n\n");
        sb.append("Por Categoria:\n");
        for (ItemRelatorioCategoria item : itens) {
            sb.append(String.format("  %s | Previsto: %s | Realizado: %s | Variação: %.2f%% | %s%n",
                    item.getCategoria(),
                    item.getValorPrevisto(),
                    item.getValorRealizado(),
                    item.getPercentualVariacao(),
                    item.getClassificacao()));
        }
        return sb.toString();
    }
}

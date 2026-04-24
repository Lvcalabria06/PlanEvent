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

/**
 * RN1: Dados buscados via repositórios — a implementação JPQL fica na infra.
 * RN2: Consolida total geral, itens por categoria e percentual de variação.
 * RN3: RelatorioFinanceiro registra dataGeracao e geradoPorUsuarioId automaticamente.
 * RN4: Impede geração se orçamento por categoria não estiver cadastrado.
 * RN5: Snapshot do estado atual — imutável após persistência.
 * RN6: Cada geração cria um novo RelatorioFinanceiro com id próprio.
 * RN7: Entidade sem setters — imutável por design.
 * RN8: Cálculo delegado ao ItemRelatorioCategoria.
 */
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

        // RN4: evento deve existir
        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evento inválido ou não encontrado."));

        // RN4: orçamento do evento deve estar cadastrado
        OrcamentoEvento orcamento = orcamentoEventoRepository
                .buscarPorEventoId(eventoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. " +
                        "Cadastre o orçamento previsto por categoria antes de gerar o relatório."));

        // RN4: deve haver ao menos uma categoria com orçamento previsto
        List<CategoriaOrcamento> categorias = categoriaOrcamentoRepository
                .listarPorOrcamentoId(orcamento.getId());

        if (categorias == null || categorias.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível gerar o relatório pois não há orçamento " +
                    "previsto cadastrado por categoria para este evento.");
        }

        // RN1 + RN2 + RN5: busca o estado atual das despesas por categoria (snapshot)
        List<ItemRelatorioCategoria> itens = new ArrayList<>();
        BigDecimal totalGeralPrevisto = BigDecimal.ZERO;
        BigDecimal totalGeralRealizado = BigDecimal.ZERO;

        for (CategoriaOrcamento cat : categorias) {
            // RN1: dados buscados do banco via repositório (JPQL na infra)
            BigDecimal realizado = despesaRepository
                    .somarValoresPorEventoECategoria(eventoId, cat.getNome());

            BigDecimal realizadoFinal = realizado != null ? realizado : BigDecimal.ZERO;

            // RN2 + RN8: ItemRelatorioCategoria calcula percentual automaticamente
            itens.add(new ItemRelatorioCategoria(
                    cat.getNome(),
                    cat.getValorPrevisto(),
                    realizadoFinal));

            totalGeralPrevisto = totalGeralPrevisto.add(cat.getValorPrevisto());
            totalGeralRealizado = totalGeralRealizado.add(realizadoFinal);
        }

        // Conteúdo textual para auditoria
        String conteudo = construirConteudo(eventoId, totalGeralPrevisto, totalGeralRealizado, itens);

        // RN3 + RN5 + RN6: cria novo relatório imutável com id único e dataGeracao automática
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

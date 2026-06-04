package domain.financeiro.template;

import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.exception.CategoriaOrcamentoEsgotadaException;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;
import domain.financeiro.valueobject.StatusDespesa;

import java.math.BigDecimal;

/**
 * Template Method para validação de limites orçamentários (RN10, RN11).
 * O fluxo de cálculo é fixo; subclasses definem os percentuais de bloqueio e aprovação.
 */
public abstract class ValidadorLimiteOrcamentarioTemplateMethod {

    private final DespesaRepository despesaRepository;
    private final OrcamentoEventoRepository orcamentoEventoRepository;
    private final CategoriaOrcamentoRepository categoriaOrcamentoRepository;

    protected ValidadorLimiteOrcamentarioTemplateMethod(DespesaRepository despesaRepository,
                                                         OrcamentoEventoRepository orcamentoEventoRepository,
                                                         CategoriaOrcamentoRepository categoriaOrcamentoRepository) {
        this.despesaRepository = despesaRepository;
        this.orcamentoEventoRepository = orcamentoEventoRepository;
        this.categoriaOrcamentoRepository = categoriaOrcamentoRepository;
    }

    public final void validar(Despesa despesa, BigDecimal valorAtualDespesa, BigDecimal valorConsiderado) {
        CategoriaOrcamento categoriaOrc = obterCategoriaOrcamento(despesa);
        BigDecimal totalAtivo = totalAtivoOuZero(
                despesaRepository.somarValoresAtivosPorEventoECategoria(
                        despesa.getEventoId(), despesa.getCategoria()));

        BigDecimal totalSemDespesa = totalAtivo.subtract(valorAtualDespesa);
        BigDecimal novoTotal = totalSemDespesa.add(valorConsiderado);
        BigDecimal previsto = categoriaOrc.getValorPrevisto();

        verificarUltrapassagemOrcamento(despesa, totalSemDespesa, valorConsiderado, previsto, novoTotal);
        aplicarRegraPendenteAprovacao(despesa, previsto, novoTotal);
    }

    private CategoriaOrcamento obterCategoriaOrcamento(Despesa despesa) {
        var orcamento = orcamentoEventoRepository.buscarPorEventoId(despesa.getEventoId())
                .orElseThrow(() -> new IllegalStateException(
                        "Orçamento não encontrado para o evento. Cadastre o orçamento antes de registrar despesas."));

        return categoriaOrcamentoRepository
                .buscarPorOrcamentoECategoria(orcamento.getId(), despesa.getCategoria())
                .orElseThrow(() -> new IllegalStateException(
                        "Categoria '" + despesa.getCategoria()
                                + "' não possui orçamento previsto cadastrado para este evento."));
    }

    private void verificarUltrapassagemOrcamento(Despesa despesa,
                                                  BigDecimal totalSemDespesa,
                                                  BigDecimal valorConsiderado,
                                                  BigDecimal previsto,
                                                  BigDecimal novoTotal) {
        if (novoTotal.compareTo(previsto.multiply(percentualBloqueio())) > 0) {
            throw new CategoriaOrcamentoEsgotadaException(
                    "Despesa bloqueada: o total acumulado de " + totalSemDespesa
                            + " mais o valor de " + valorConsiderado
                            + " ultrapassa o orçamento previsto de " + previsto
                            + " para a categoria '" + despesa.getCategoria() + "'.");
        }
    }

    private void aplicarRegraPendenteAprovacao(Despesa despesa, BigDecimal previsto, BigDecimal novoTotal) {
        if (despesa.getStatus() == StatusDespesa.REGISTRADA
                && novoTotal.compareTo(previsto.multiply(percentualAprovacao())) >= 0) {
            despesa.marcarPendente();
        }
    }

    protected abstract BigDecimal percentualBloqueio();

    protected abstract BigDecimal percentualAprovacao();

    private static BigDecimal totalAtivoOuZero(BigDecimal total) {
        return total != null ? total : BigDecimal.ZERO;
    }
}

package domain.financeiro.template;

import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.repository.DespesaRepository;
import domain.financeiro.repository.OrcamentoEventoRepository;

import java.math.BigDecimal;

public class ValidadorLimitePadraoTemplateMethod extends ValidadorLimiteOrcamentarioTemplateMethod {

    private static final BigDecimal PERCENTUAL_BLOQUEIO = new BigDecimal("1.00");
    private static final BigDecimal PERCENTUAL_APROVACAO = new BigDecimal("0.80");

    public ValidadorLimitePadraoTemplateMethod(DespesaRepository despesaRepository,
                                                 OrcamentoEventoRepository orcamentoEventoRepository,
                                                 CategoriaOrcamentoRepository categoriaOrcamentoRepository) {
        super(despesaRepository, orcamentoEventoRepository, categoriaOrcamentoRepository);
    }

    @Override
    protected BigDecimal percentualBloqueio() {
        return PERCENTUAL_BLOQUEIO;
    }

    @Override
    protected BigDecimal percentualAprovacao() {
        return PERCENTUAL_APROVACAO;
    }
}

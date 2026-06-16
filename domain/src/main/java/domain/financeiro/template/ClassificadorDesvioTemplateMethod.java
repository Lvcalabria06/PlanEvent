package domain.financeiro.template;

import domain.financeiro.valueobject.ClassificacaoDesvio;

/**
 * Template Method para classificação do desvio orçamentário (RN6).
 */
public abstract class ClassificadorDesvioTemplateMethod {

    public final ClassificacaoDesvio classificar(double desvioPercentual) {
        if (desvioPercentual > limiteCritico()) {
            return ClassificacaoDesvio.CRITICO;
        }
        if (desvioPercentual >= limiteAtencao()) {
            return ClassificacaoDesvio.ATENCAO;
        }
        return ClassificacaoDesvio.NORMAL;
    }

    protected abstract double limiteAtencao();

    protected abstract double limiteCritico();
}

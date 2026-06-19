package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão composta que representa a conjunção lógica (AND) de duas
 * sub-expressões. Retorna {@code true} apenas quando ambas as partes
 * são satisfeitas pelo local avaliado.
 */
public class ExpressaoAnd implements ExpressaoLocal {

    private final ExpressaoLocal esquerda;
    private final ExpressaoLocal direita;

    public ExpressaoAnd(ExpressaoLocal esquerda, ExpressaoLocal direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(Local local) {
        return esquerda.interpretar(local) && direita.interpretar(local);
    }
}

package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão composta que representa a disjunção lógica (OR) de duas
 * sub-expressões. Retorna {@code true} quando ao menos uma das partes
 * é satisfeita pelo local avaliado.
 */
public class ExpressaoOr implements ExpressaoLocal {

    private final ExpressaoLocal esquerda;
    private final ExpressaoLocal direita;

    public ExpressaoOr(ExpressaoLocal esquerda, ExpressaoLocal direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(Local local) {
        return esquerda.interpretar(local) || direita.interpretar(local);
    }
}

package domain.funcionario.interpreter;

import domain.funcionario.entity.Funcionario;

/**
 * Interface do padrão Interpreter para filtragem de funcionários.
 * Cada implementação representa um nó (terminal ou não-terminal) de uma
 * expressão lógica aplicada sobre um {@link Funcionario}.
 */
public interface ExpressaoFuncionario {
    boolean interpretar(Funcionario funcionario);
}

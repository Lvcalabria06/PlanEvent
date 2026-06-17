package domain.funcionario.interpreter;

public class ExpressaoOrFuncionario implements ExpressaoFuncionario {

    private final ExpressaoFuncionario esquerda;
    private final ExpressaoFuncionario direita;

    public ExpressaoOrFuncionario(ExpressaoFuncionario esquerda, ExpressaoFuncionario direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(domain.funcionario.entity.Funcionario funcionario) {
        return esquerda.interpretar(funcionario) || direita.interpretar(funcionario);
    }
}

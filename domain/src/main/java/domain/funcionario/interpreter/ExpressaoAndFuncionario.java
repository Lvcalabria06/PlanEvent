package domain.funcionario.interpreter;

public class ExpressaoAndFuncionario implements ExpressaoFuncionario {

    private final ExpressaoFuncionario esquerda;
    private final ExpressaoFuncionario direita;

    public ExpressaoAndFuncionario(ExpressaoFuncionario esquerda, ExpressaoFuncionario direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(domain.funcionario.entity.Funcionario funcionario) {
        return esquerda.interpretar(funcionario) && direita.interpretar(funcionario);
    }
}

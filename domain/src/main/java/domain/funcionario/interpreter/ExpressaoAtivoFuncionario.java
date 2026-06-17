package domain.funcionario.interpreter;

import domain.funcionario.entity.Funcionario;

public class ExpressaoAtivoFuncionario implements ExpressaoFuncionario {

    private final boolean ativoEsperado;

    public ExpressaoAtivoFuncionario(boolean ativo) {
        this.ativoEsperado = ativo;
    }

    @Override
    public boolean interpretar(Funcionario funcionario) {
        return funcionario.isAtivo() == ativoEsperado;
    }
}

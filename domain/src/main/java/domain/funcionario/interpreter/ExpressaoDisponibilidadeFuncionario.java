package domain.funcionario.interpreter;

import domain.funcionario.entity.Funcionario;
import domain.funcionario.valueobject.DisponibilidadeFuncionario;

public class ExpressaoDisponibilidadeFuncionario implements ExpressaoFuncionario {

    private final DisponibilidadeFuncionario disponibilidadeEsperada;

    public ExpressaoDisponibilidadeFuncionario(String disponibilidade) {
        this.disponibilidadeEsperada = DisponibilidadeFuncionario.fromString(disponibilidade);
    }

    @Override
    public boolean interpretar(Funcionario funcionario) {
        return funcionario.getDisponibilidade() == disponibilidadeEsperada;
    }
}

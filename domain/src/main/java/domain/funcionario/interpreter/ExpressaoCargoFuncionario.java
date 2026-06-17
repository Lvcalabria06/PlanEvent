package domain.funcionario.interpreter;

import domain.funcionario.entity.Funcionario;
import domain.funcionario.valueobject.CargoFuncionario;

public class ExpressaoCargoFuncionario implements ExpressaoFuncionario {

    private final CargoFuncionario cargoEsperado;

    public ExpressaoCargoFuncionario(String cargo) {
        this.cargoEsperado = CargoFuncionario.fromString(cargo);
    }

    @Override
    public boolean interpretar(Funcionario funcionario) {
        return funcionario.getCargo() == cargoEsperado;
    }
}

package domain.equipe.interpreter;

import domain.equipe.entity.MembroEquipe;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.funcionario.valueobject.CargoFuncionario;

public class ExpressaoCargo implements ExpressaoMembro {
    private final CargoFuncionario cargoEsperado;

    public ExpressaoCargo(String cargo) {
        this.cargoEsperado = CargoFuncionario.fromString(cargo);
    }

    @Override
    public boolean interpretar(MembroEquipe membro, FuncionarioRepository funcionarioRepository) {
        Funcionario funcionario = funcionarioRepository.buscarPorId(membro.getFuncionarioId())
                .orElse(null);
        if (funcionario == null) {
            return false;
        }
        return funcionario.getCargo() == cargoEsperado;
    }
}

package domain.equipe.interpreter;

import domain.equipe.entity.MembroEquipe;
import domain.funcionario.repository.FuncionarioRepository;

public class ExpressaoLider implements ExpressaoMembro {
    private final boolean valorEsperado;

    public ExpressaoLider(boolean valorEsperado) {
        this.valorEsperado = valorEsperado;
    }

    @Override
    public boolean interpretar(MembroEquipe membro, FuncionarioRepository funcionarioRepository) {
        return membro.isLider() == valorEsperado;
    }
}

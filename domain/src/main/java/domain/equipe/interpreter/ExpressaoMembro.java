package domain.equipe.interpreter;

import domain.equipe.entity.MembroEquipe;
import domain.funcionario.repository.FuncionarioRepository;

public interface ExpressaoMembro {
    boolean interpretar(MembroEquipe membro, FuncionarioRepository funcionarioRepository);
}

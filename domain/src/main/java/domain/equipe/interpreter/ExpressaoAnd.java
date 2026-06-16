package domain.equipe.interpreter;

import domain.equipe.entity.MembroEquipe;
import domain.funcionario.repository.FuncionarioRepository;

public class ExpressaoAnd implements ExpressaoMembro {
    private final ExpressaoMembro esquerda;
    private final ExpressaoMembro direita;

    public ExpressaoAnd(ExpressaoMembro esquerda, ExpressaoMembro direita) {
        this.esquerda = esquerda;
        this.direita = direita;
    }

    @Override
    public boolean interpretar(MembroEquipe membro, FuncionarioRepository funcionarioRepository) {
        return esquerda.interpretar(membro, funcionarioRepository) && direita.interpretar(membro, funcionarioRepository);
    }
}

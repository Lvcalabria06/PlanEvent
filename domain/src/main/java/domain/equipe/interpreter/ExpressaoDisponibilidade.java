package domain.equipe.interpreter;

import domain.equipe.entity.MembroEquipe;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import domain.funcionario.valueobject.DisponibilidadeFuncionario;

public class ExpressaoDisponibilidade implements ExpressaoMembro {
    private final DisponibilidadeFuncionario disponibilidadeEsperada;

    public ExpressaoDisponibilidade(String disponibilidade) {
        this.disponibilidadeEsperada = DisponibilidadeFuncionario.fromString(disponibilidade);
    }

    @Override
    public boolean interpretar(MembroEquipe membro, FuncionarioRepository funcionarioRepository) {
        Funcionario funcionario = funcionarioRepository.buscarPorId(membro.getFuncionarioId())
                .orElse(null);
        if (funcionario == null) {
            return false;
        }
        return funcionario.getDisponibilidade() == disponibilidadeEsperada;
    }
}

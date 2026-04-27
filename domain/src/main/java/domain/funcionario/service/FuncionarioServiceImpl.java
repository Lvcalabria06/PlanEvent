package domain.funcionario.service;

import domain.equipe.repository.EquipeRepository;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;

import java.util.List;

public class FuncionarioServiceImpl implements FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final EquipeRepository equipeRepository;

    public FuncionarioServiceImpl(FuncionarioRepository funcionarioRepository,
                                  EquipeRepository equipeRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.equipeRepository = equipeRepository;
    }

    @Override
    public Funcionario criarFuncionario(Funcionario funcionario) {
        return funcionarioRepository.salvar(funcionario);
    }

    @Override
    public Funcionario editarFuncionario(Funcionario funcionarioEditado) {
        Funcionario atual = funcionarioRepository.buscarPorId(funcionarioEditado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        atual.alterarNome(funcionarioEditado.getNome());
        atual.alterarCargo(funcionarioEditado.getCargo().name());
        atual.alterarDisponibilidade(funcionarioEditado.getDisponibilidade().name());

        return funcionarioRepository.salvar(atual);
    }

    @Override
    public Funcionario buscarFuncionario(String id) {
        return funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
    }

    @Override
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.listarTodos();
    }

    @Override
    public void inativarFuncionario(String id) {
        Funcionario funcionario = funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        if (equipeRepository.existeFuncionarioVinculado(id)) {
            throw new IllegalStateException("Funcionário não pode ser removido, pois está vinculado a uma equipe.");
        }

        funcionario.inativar();
        funcionarioRepository.salvar(funcionario);
    }
}
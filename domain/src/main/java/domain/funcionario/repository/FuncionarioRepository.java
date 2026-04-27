package domain.funcionario.repository;

import domain.funcionario.entity.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {
    Funcionario salvar(Funcionario funcionario);
    Optional<Funcionario> buscarPorId(String id);
    List<Funcionario> listarTodos();
}
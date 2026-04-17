package domain.funcionario.repository;

import java.util.Optional;

import domain.funcionario.entity.Funcionario;

public interface FuncionarioRepository {
    Optional<Funcionario> buscarPorId(String id);
}

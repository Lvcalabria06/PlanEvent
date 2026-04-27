package domain.funcionario.service;

import domain.funcionario.entity.Funcionario;

import java.util.List;

public interface FuncionarioService {
    Funcionario criarFuncionario(Funcionario funcionario);
    Funcionario editarFuncionario(Funcionario funcionario);
    Funcionario buscarFuncionario(String id);
    List<Funcionario> listarFuncionarios();
    void inativarFuncionario(String id);
}
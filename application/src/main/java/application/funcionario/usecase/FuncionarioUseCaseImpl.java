package application.funcionario.usecase;

import application.funcionario.dto.CadastrarFuncionarioRequest;
import application.funcionario.dto.EditarFuncionarioRequest;
import application.funcionario.dto.FuncionarioResponse;
import application.funcionario.mapper.FuncionarioDtoMapper;
import domain.funcionario.entity.Funcionario;
import domain.funcionario.service.FuncionarioService;

import java.util.List;

public class FuncionarioUseCaseImpl implements FuncionarioUseCase {

    private final FuncionarioService funcionarioService;

    public FuncionarioUseCaseImpl(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @Override
    public FuncionarioResponse cadastrar(CadastrarFuncionarioRequest request) {
        Funcionario novo = new Funcionario(request.nome(), request.cargo(), request.disponibilidade());
        return FuncionarioDtoMapper.paraResposta(funcionarioService.criarFuncionario(novo));
    }

    @Override
    public FuncionarioResponse editar(String funcionarioId, EditarFuncionarioRequest request) {
        Funcionario atual = funcionarioService.buscarFuncionario(funcionarioId);
        atual.alterarNome(request.nome());
        atual.alterarCargo(request.cargo());
        atual.alterarDisponibilidade(request.disponibilidade());
        return FuncionarioDtoMapper.paraResposta(funcionarioService.editarFuncionario(atual));
    }

    @Override
    public FuncionarioResponse buscar(String funcionarioId) {
        return FuncionarioDtoMapper.paraResposta(funcionarioService.buscarFuncionario(funcionarioId));
    }

    @Override
    public List<FuncionarioResponse> listar() {
        return funcionarioService.listarFuncionarios().stream()
                .map(FuncionarioDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public void inativar(String funcionarioId) {
        funcionarioService.inativarFuncionario(funcionarioId);
    }

    @Override
    public List<FuncionarioResponse> filtrar(String expressao) {
        return funcionarioService.filtrarFuncionarios(expressao).stream()
                .map(FuncionarioDtoMapper::paraResposta)
                .toList();
    }
}

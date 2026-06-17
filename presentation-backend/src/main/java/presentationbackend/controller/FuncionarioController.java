package presentationbackend.controller;

import application.funcionario.dto.CadastrarFuncionarioRequest;
import application.funcionario.dto.EditarFuncionarioRequest;
import application.funcionario.dto.FiltrarFuncionariosRequest;
import application.funcionario.dto.FuncionarioResponse;
import application.funcionario.usecase.FuncionarioUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de gerenciamento de funcionários.
 */
@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioUseCase funcionarioUseCase;

    public FuncionarioController(FuncionarioUseCase funcionarioUseCase) {
        this.funcionarioUseCase = funcionarioUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponse cadastrar(@RequestBody CadastrarFuncionarioRequest request) {
        return funcionarioUseCase.cadastrar(request);
    }

    @GetMapping("/{id}")
    public FuncionarioResponse buscar(@PathVariable String id) {
        return funcionarioUseCase.buscar(id);
    }

    @GetMapping
    public List<FuncionarioResponse> listar() {
        return funcionarioUseCase.listar();
    }

    @PutMapping("/{id}")
    public FuncionarioResponse editar(@PathVariable String id, @RequestBody EditarFuncionarioRequest request) {
        return funcionarioUseCase.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable String id) {
        funcionarioUseCase.inativar(id);
    }

    @PostMapping("/filtrar")
    public List<FuncionarioResponse> filtrar(@RequestBody FiltrarFuncionariosRequest request) {
        return funcionarioUseCase.filtrar(request.expressao());
    }
}

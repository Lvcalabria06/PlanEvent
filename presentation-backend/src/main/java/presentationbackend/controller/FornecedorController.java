package presentationbackend.controller;

import application.fornecedor.dto.CadastrarFornecedorRequest;
import application.fornecedor.dto.EditarFornecedorRequest;
import application.fornecedor.dto.FornecedorResponse;
import application.fornecedor.usecase.FornecedorUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fornecedores")
public class FornecedorController {

    private final FornecedorUseCase fornecedorUseCase;

    public FornecedorController(FornecedorUseCase fornecedorUseCase) {
        this.fornecedorUseCase = fornecedorUseCase;
    }

    @GetMapping
    public List<FornecedorResponse> listar() {
        return fornecedorUseCase.listar();
    }

    @GetMapping("/{id}")
    public FornecedorResponse buscar(@PathVariable String id) {
        return fornecedorUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorResponse cadastrar(@RequestBody CadastrarFornecedorRequest request) {
        return fornecedorUseCase.cadastrar(request);
    }

    @PutMapping("/{id}")
    public FornecedorResponse editar(@PathVariable String id,
                                     @RequestBody EditarFornecedorRequest request) {
        return fornecedorUseCase.editar(id, request);
    }

    @PostMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable String id) {
        fornecedorUseCase.desativar(id);
    }
}

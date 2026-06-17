package presentationbackend.controller;

import application.estoque.dto.AdicionarEstoqueRequest;
import application.estoque.dto.CadastrarItemEstoqueRequest;
import application.estoque.dto.CadastrarSubstituicaoRequest;
import application.estoque.dto.EditarItemEstoqueRequest;
import application.estoque.dto.ItemEstoqueResponse;
import application.estoque.dto.ItemSubstituicaoResponse;
import application.estoque.usecase.ItemEstoqueUseCase;
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
@RequestMapping("/api/itens-estoque")
public class ItemEstoqueController {

    private final ItemEstoqueUseCase itemEstoqueUseCase;

    public ItemEstoqueController(ItemEstoqueUseCase itemEstoqueUseCase) {
        this.itemEstoqueUseCase = itemEstoqueUseCase;
    }

    @GetMapping
    public List<ItemEstoqueResponse> listarTodos() {
        return itemEstoqueUseCase.listarTodos();
    }

    @GetMapping("/ativos")
    public List<ItemEstoqueResponse> listarAtivos() {
        return itemEstoqueUseCase.listarAtivos();
    }

    @GetMapping("/substituicoes")
    public List<ItemSubstituicaoResponse> listarSubstituicoes() {
        return itemEstoqueUseCase.listarSubstituicoes();
    }

    @PostMapping("/substituicoes")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemSubstituicaoResponse cadastrarSubstituicao(@RequestBody CadastrarSubstituicaoRequest request) {
        return itemEstoqueUseCase.cadastrarSubstituicao(request);
    }

    @GetMapping("/{id}")
    public ItemEstoqueResponse buscar(@PathVariable String id) {
        return itemEstoqueUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemEstoqueResponse criar(@RequestBody CadastrarItemEstoqueRequest request) {
        return itemEstoqueUseCase.cadastrar(request);
    }

    @PutMapping("/{id}")
    public ItemEstoqueResponse editar(@PathVariable String id, @RequestBody EditarItemEstoqueRequest request) {
        return itemEstoqueUseCase.editar(id, request);
    }

    @PostMapping("/{id}/adicionar")
    public ItemEstoqueResponse adicionarEstoque(@PathVariable String id, @RequestBody AdicionarEstoqueRequest request) {
        return itemEstoqueUseCase.adicionarEstoque(id, request);
    }

    @PostMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable String id) {
        itemEstoqueUseCase.desativar(id);
    }

    @PostMapping("/{id}/reativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reativar(@PathVariable String id) {
        itemEstoqueUseCase.reativar(id);
    }
}

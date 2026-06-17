package presentationbackend.controller;

import application.estoque.dto.AplicarRedistribuicaoRequest;
import application.estoque.dto.CenarioRedistribuicaoResponse;
import application.estoque.dto.GerarCenarioRedistribuicaoRequest;
import application.estoque.dto.InvalidarCenarioRequest;
import application.estoque.usecase.RedistribuicaoEstoqueUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cenarios-redistribuicao")
public class CenarioRedistribuicaoController {

    private final RedistribuicaoEstoqueUseCase redistribuicaoEstoqueUseCase;

    public CenarioRedistribuicaoController(RedistribuicaoEstoqueUseCase redistribuicaoEstoqueUseCase) {
        this.redistribuicaoEstoqueUseCase = redistribuicaoEstoqueUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CenarioRedistribuicaoResponse gerar(@RequestBody GerarCenarioRedistribuicaoRequest request) {
        return redistribuicaoEstoqueUseCase.gerar(request);
    }

    @GetMapping
    public List<CenarioRedistribuicaoResponse> listarTodos() {
        return redistribuicaoEstoqueUseCase.listarTodos();
    }

    @GetMapping("/pendentes")
    public List<CenarioRedistribuicaoResponse> listarPendentes() {
        return redistribuicaoEstoqueUseCase.listarPendentes();
    }

    @GetMapping("/{id}")
    public CenarioRedistribuicaoResponse buscar(@PathVariable String id) {
        return redistribuicaoEstoqueUseCase.buscar(id);
    }

    @PostMapping("/{id}/aplicar")
    public CenarioRedistribuicaoResponse aplicar(@PathVariable String id,
                                                 @RequestBody AplicarRedistribuicaoRequest request) {
        return redistribuicaoEstoqueUseCase.aplicar(id, request);
    }

    @PostMapping("/{id}/invalidar")
    public CenarioRedistribuicaoResponse invalidar(@PathVariable String id,
                                                   @RequestBody InvalidarCenarioRequest request) {
        return redistribuicaoEstoqueUseCase.invalidar(id, request);
    }
}

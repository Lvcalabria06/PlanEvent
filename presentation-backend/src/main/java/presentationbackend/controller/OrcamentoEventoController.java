package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.usecase.OrcamentoEventoUseCase;
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

/**
 * Gerencia o orçamento previsto por categoria de um evento.
 * Base: /api/v1/eventos/{eventoId}/financeiro/orcamento
 */
@RestController
@RequestMapping("/api/v1/eventos/{eventoId}/financeiro/orcamento")
public class OrcamentoEventoController {

    private final OrcamentoEventoUseCase orcamentoUseCase;

    public OrcamentoEventoController(OrcamentoEventoUseCase orcamentoUseCase) {
        this.orcamentoUseCase = orcamentoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.OrcamentoEventoDto criar(
            @PathVariable String eventoId,
            @RequestBody FinanceiroDtos.CriarOrcamentoRequest request) {
        return orcamentoUseCase.criar(eventoId, request);
    }

    @GetMapping
    public FinanceiroDtos.OrcamentoEventoDto buscar(@PathVariable String eventoId) {
        return orcamentoUseCase.buscar(eventoId);
    }

    @GetMapping("/categorias")
    public List<FinanceiroDtos.CategoriaOrcamentoDto> listarCategorias(@PathVariable String eventoId) {
        return orcamentoUseCase.listarCategorias(eventoId);
    }

    @PostMapping("/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.CategoriaOrcamentoDto adicionarCategoria(
            @PathVariable String eventoId,
            @RequestBody FinanceiroDtos.AdicionarCategoriaOrcamentoRequest request) {
        return orcamentoUseCase.adicionarCategoria(eventoId, request);
    }

    @PutMapping("/categorias/{categoria}")
    public FinanceiroDtos.CategoriaOrcamentoDto atualizarCategoria(
            @PathVariable String eventoId,
            @PathVariable String categoria,
            @RequestBody FinanceiroDtos.AtualizarCategoriaOrcamentoRequest request) {
        return orcamentoUseCase.atualizarCategoria(eventoId, categoria, request);
    }
}

package presentationbackend.controller;

import application.estoque.dto.AjustarPrevisaoRequest;
import application.estoque.dto.GerarPrevisaoRequest;
import application.estoque.dto.PrevisaoConsumoResponse;
import application.estoque.usecase.PrevisaoConsumoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de previsão de consumo de estoque.
 */
@RestController
@RequestMapping("/api/previsoes-consumo")
public class PrevisaoConsumoController {

    private final PrevisaoConsumoUseCase previsaoConsumoUseCase;

    public PrevisaoConsumoController(PrevisaoConsumoUseCase previsaoConsumoUseCase) {
        this.previsaoConsumoUseCase = previsaoConsumoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrevisaoConsumoResponse gerar(@RequestBody GerarPrevisaoRequest request) {
        return previsaoConsumoUseCase.gerar(request);
    }

    @GetMapping
    public List<PrevisaoConsumoResponse> listarTodas() {
        return previsaoConsumoUseCase.listarTodas();
    }

    @GetMapping("/por-evento/{eventoId}")
    public PrevisaoConsumoResponse buscarPorEvento(@PathVariable String eventoId) {
        return previsaoConsumoUseCase.buscarPorEvento(eventoId);
    }

    @GetMapping("/{id}")
    public PrevisaoConsumoResponse buscar(@PathVariable String id) {
        return previsaoConsumoUseCase.buscar(id);
    }

    @PutMapping("/{id}/ajustar")
    public PrevisaoConsumoResponse ajustar(@PathVariable String id, @RequestBody AjustarPrevisaoRequest request) {
        return previsaoConsumoUseCase.ajustar(id, request);
    }

    @PostMapping("/{id}/recalcular")
    public PrevisaoConsumoResponse recalcular(@PathVariable String id, @RequestParam String usuarioId) {
        return previsaoConsumoUseCase.recalcular(id, usuarioId);
    }

    @PostMapping("/por-evento/{eventoId}/invalidar")
    public PrevisaoConsumoResponse invalidarPorEvento(@PathVariable String eventoId, @RequestParam String usuarioId) {
        return previsaoConsumoUseCase.invalidarPorEvento(eventoId, usuarioId);
    }
}

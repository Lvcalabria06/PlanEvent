package presentationbackend.controller;

import application.estoque.dto.ConsumoEventoResponse;
import application.estoque.dto.RegistrarConsumoEventoRequest;
import application.estoque.usecase.ConsumoEventoUseCase;
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
@RequestMapping("/api/consumos-evento")
public class ConsumoEventoController {

    private final ConsumoEventoUseCase consumoEventoUseCase;

    public ConsumoEventoController(ConsumoEventoUseCase consumoEventoUseCase) {
        this.consumoEventoUseCase = consumoEventoUseCase;
    }

    @GetMapping
    public List<ConsumoEventoResponse> listarTodos() {
        return consumoEventoUseCase.listarTodos();
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<ConsumoEventoResponse> listarPorEvento(@PathVariable String eventoId) {
        return consumoEventoUseCase.listarPorEvento(eventoId);
    }

    @GetMapping("/{id}")
    public ConsumoEventoResponse buscar(@PathVariable String id) {
        return consumoEventoUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumoEventoResponse registrar(@RequestBody RegistrarConsumoEventoRequest request) {
        return consumoEventoUseCase.registrar(request);
    }

    @PostMapping("/{id}/invalidar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void invalidar(@PathVariable String id) {
        consumoEventoUseCase.invalidar(id);
    }
}

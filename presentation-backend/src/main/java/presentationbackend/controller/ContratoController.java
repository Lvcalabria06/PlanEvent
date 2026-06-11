package presentationbackend.controller;

import application.contrato.dto.ContratoResponse;
import application.contrato.dto.CriarContratoRequest;
import application.contrato.dto.EditarContratoRequest;
import application.contrato.usecase.ContratoUseCase;
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
@RequestMapping("/api/v1/contratos")
public class ContratoController {

    private final ContratoUseCase contratoUseCase;

    public ContratoController(ContratoUseCase contratoUseCase) {
        this.contratoUseCase = contratoUseCase;
    }

    @GetMapping
    public List<ContratoResponse> listar() {
        return contratoUseCase.listar();
    }

    @GetMapping("/{id}")
    public ContratoResponse buscar(@PathVariable String id) {
        return contratoUseCase.buscar(id);
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<ContratoResponse> listarPorEvento(@PathVariable String eventoId) {
        return contratoUseCase.listarPorEvento(eventoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratoResponse criar(@RequestBody CriarContratoRequest request) {
        return contratoUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public ContratoResponse editar(@PathVariable String id,
                                   @RequestBody EditarContratoRequest request) {
        return contratoUseCase.editar(id, request);
    }

    @PostMapping("/{id}/encerrar")
    public ContratoResponse encerrar(@PathVariable String id) {
        return contratoUseCase.encerrar(id);
    }
}

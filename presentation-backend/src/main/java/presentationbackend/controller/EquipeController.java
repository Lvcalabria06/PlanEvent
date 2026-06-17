package presentationbackend.controller;

import application.equipe.dto.CriarEquipeRequest;
import application.equipe.dto.EditarEquipeRequest;
import application.equipe.dto.EquipeResponse;
import application.equipe.dto.FiltrarMembrosRequest;
import application.equipe.dto.MembroEquipeResponse;
import application.equipe.usecase.EquipeUseCase;
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
 * Endpoints REST de gerenciamento de equipes.
 */
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeUseCase equipeUseCase;

    public EquipeController(EquipeUseCase equipeUseCase) {
        this.equipeUseCase = equipeUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipeResponse criar(@RequestBody CriarEquipeRequest request) {
        return equipeUseCase.criar(request);
    }

    @GetMapping("/{id}")
    public EquipeResponse buscar(@PathVariable String id) {
        return equipeUseCase.buscar(id);
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<EquipeResponse> listarPorEvento(@PathVariable String eventoId) {
        return equipeUseCase.listarPorEvento(eventoId);
    }

    @PutMapping("/{id}")
    public EquipeResponse editar(@PathVariable String id, @RequestBody EditarEquipeRequest request) {
        return equipeUseCase.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        equipeUseCase.remover(id);
    }

    @PostMapping("/{id}/filtrar-membros")
    public List<MembroEquipeResponse> filtrarMembros(@PathVariable String id,
            @RequestBody FiltrarMembrosRequest request) {
        return equipeUseCase.filtrarMembros(id, request.expressao());
    }
}

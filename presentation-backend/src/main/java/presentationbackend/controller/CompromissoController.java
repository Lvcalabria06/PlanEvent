package presentationbackend.controller;

import application.agenda.dto.CompromissoResponse;
import application.agenda.dto.CriarCompromissoRequest;
import application.agenda.dto.EditarCompromissoRequest;
import application.agenda.usecase.CompromissoUseCase;
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

@RestController
@RequestMapping("/api/compromissos")
public class CompromissoController {

    private final CompromissoUseCase compromissoUseCase;

    public CompromissoController(CompromissoUseCase compromissoUseCase) {
        this.compromissoUseCase = compromissoUseCase;
    }

    @GetMapping
    public List<CompromissoResponse> listarTodos() {
        return compromissoUseCase.listarTodos();
    }

    @GetMapping("/por-gestor/{gestorId}")
    public List<CompromissoResponse> listarPorGestor(@PathVariable String gestorId) {
        return compromissoUseCase.listarPorGestor(gestorId);
    }

    @GetMapping("/{id}")
    public CompromissoResponse buscar(@PathVariable String id) {
        return compromissoUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompromissoResponse criar(@RequestBody CriarCompromissoRequest request) {
        return compromissoUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public CompromissoResponse editar(@PathVariable String id, @RequestBody EditarCompromissoRequest request) {
        return compromissoUseCase.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        compromissoUseCase.remover(id);
    }

    @PostMapping("/{id}/iniciar")
    public CompromissoResponse iniciar(@PathVariable String id) {
        return compromissoUseCase.iniciar(id);
    }

    @PostMapping("/{id}/concluir")
    public CompromissoResponse concluir(@PathVariable String id) {
        return compromissoUseCase.concluir(id);
    }

    @PostMapping("/{id}/cancelar")
    public CompromissoResponse cancelar(@PathVariable String id) {
        return compromissoUseCase.cancelar(id);
    }
}

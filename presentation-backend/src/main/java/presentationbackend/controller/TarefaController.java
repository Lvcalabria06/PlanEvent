package presentationbackend.controller;

import application.tarefa.dto.AtribuirResponsavelRequest;
import application.tarefa.dto.CriarTarefaRequest;
import application.tarefa.dto.EditarTarefaRequest;
import application.tarefa.dto.TarefaResponse;
import application.tarefa.usecase.TarefaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Endpoints REST de gerenciamento de tarefas (Funcionalidade 5 - Passo 1).
 */
@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaUseCase tarefaUseCase;

    public TarefaController(TarefaUseCase tarefaUseCase) {
        this.tarefaUseCase = tarefaUseCase;
    }

    @GetMapping
    public List<TarefaResponse> listarTodas() {
        return tarefaUseCase.listarTodas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarefaResponse criar(@RequestBody CriarTarefaRequest request) {
        return tarefaUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public TarefaResponse editar(@PathVariable String id, @RequestBody EditarTarefaRequest request) {
        return tarefaUseCase.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        tarefaUseCase.remover(id);
    }

    @PostMapping("/{id}/iniciar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void iniciar(@PathVariable String id) {
        tarefaUseCase.iniciar(id);
    }

    @PostMapping("/{id}/concluir")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void concluir(@PathVariable String id) {
        tarefaUseCase.concluir(id);
    }

    @PostMapping("/{id}/responsaveis")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atribuirResponsavel(@PathVariable String id, @RequestBody AtribuirResponsavelRequest request) {
        tarefaUseCase.atribuirResponsavel(id, request);
    }

    @DeleteMapping("/{id}/responsaveis/{funcionarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerResponsavel(@PathVariable String id, @PathVariable String funcionarioId) {
        tarefaUseCase.removerResponsavel(id, funcionarioId);
    }

    @GetMapping("/por-equipe/{equipeId}")
    public List<TarefaResponse> listarPorEquipe(@PathVariable String equipeId) {
        return tarefaUseCase.listarPorEquipe(equipeId);
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<TarefaResponse> listarPorEvento(@PathVariable String eventoId) {
        return tarefaUseCase.listarPorEvento(eventoId);
    }
}

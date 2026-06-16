package presentationbackend.controller;

import application.dependencia.dto.AdicionarDependenciaRequest;
import application.dependencia.usecase.DependenciaUseCase;
import application.tarefa.dto.TarefaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de gerenciamento de dependências entre tarefas
 * (Funcionalidade 5 - Passo 2).
 */
@RestController
@RequestMapping("/api/tarefas/{tarefaId}")
public class DependenciaController {

    private final DependenciaUseCase dependenciaUseCase;

    public DependenciaController(DependenciaUseCase dependenciaUseCase) {
        this.dependenciaUseCase = dependenciaUseCase;
    }

    @PostMapping("/dependencias")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adicionar(@PathVariable String tarefaId, @RequestBody AdicionarDependenciaRequest request) {
        dependenciaUseCase.adicionar(tarefaId, request);
    }

    @DeleteMapping("/dependencias/{predecessoraId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String tarefaId, @PathVariable String predecessoraId) {
        dependenciaUseCase.remover(tarefaId, predecessoraId);
    }

    @GetMapping("/dependencias")
    public List<TarefaResponse> listarDependencias(@PathVariable String tarefaId) {
        return dependenciaUseCase.listarDependencias(tarefaId);
    }

    @GetMapping("/dependentes")
    public List<TarefaResponse> listarDependentes(@PathVariable String tarefaId) {
        return dependenciaUseCase.listarDependentes(tarefaId);
    }
}

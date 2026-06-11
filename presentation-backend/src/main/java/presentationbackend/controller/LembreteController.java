package presentationbackend.controller;

import application.agenda.dto.AlertaLembreteResponse;
import application.agenda.dto.CriarLembreteRequest;
import application.agenda.dto.EditarLembreteRequest;
import application.agenda.dto.LembreteResponse;
import application.agenda.usecase.LembreteUseCase;
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
@RequestMapping("/api/lembretes")
public class LembreteController {

    private final LembreteUseCase lembreteUseCase;

    public LembreteController(LembreteUseCase lembreteUseCase) {
        this.lembreteUseCase = lembreteUseCase;
    }

    @GetMapping
    public List<LembreteResponse> listarTodos() {
        return lembreteUseCase.listarTodos();
    }

    @GetMapping("/por-gestor/{gestorId}")
    public List<LembreteResponse> listarPorGestor(@PathVariable String gestorId) {
        return lembreteUseCase.listarPorGestor(gestorId);
    }

    @GetMapping("/por-compromisso/{compromissoId}")
    public List<LembreteResponse> listarPorCompromisso(@PathVariable String compromissoId) {
        return lembreteUseCase.listarPorCompromisso(compromissoId);
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<LembreteResponse> listarPorEvento(@PathVariable String eventoId) {
        return lembreteUseCase.listarPorEvento(eventoId);
    }

    @GetMapping("/{id}")
    public LembreteResponse buscar(@PathVariable String id) {
        return lembreteUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LembreteResponse criar(@RequestBody CriarLembreteRequest request) {
        return lembreteUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public LembreteResponse editar(@PathVariable String id, @RequestBody EditarLembreteRequest request) {
        return lembreteUseCase.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        lembreteUseCase.remover(id);
    }

    @PostMapping("/{id}/disparar-notificacao")
    public LembreteResponse dispararNotificacao(@PathVariable String id) {
        return lembreteUseCase.dispararNotificacao(id);
    }

    @PostMapping("/processar-vencidos")
    public List<AlertaLembreteResponse> processarVencidos() {
        return lembreteUseCase.processarVencidos();
    }
}

package presentationbackend.controller;

import application.evento.dto.AlertaRiscoAlocacaoDto;
import application.evento.dto.DefinirAlocacaoLocalRequest;
import application.evento.dto.CriarEventoRequest;
import application.evento.dto.EditarEventoRequest;
import application.evento.dto.EventoResponse;
import application.evento.dto.FixarLocalPrincipalRequest;
import application.evento.dto.RegistrarAlternativasContingenciaRequest;
import application.evento.dto.RegistrarParametrosAlocacaoRequest;
import application.evento.dto.ResultadoAnaliseAlocacaoDto;
import application.evento.dto.TrocaLocalContingenciaRequest;
import application.evento.usecase.AlocacaoLocalUseCase;
import application.evento.usecase.EventoUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController("apiV1EventoController")
@RequestMapping("/api/v1/eventos")
public class EventoController {

    private final EventoUseCase eventoUseCase;
    private final AlocacaoLocalUseCase alocacaoLocalUseCase;

    public EventoController(EventoUseCase eventoUseCase, AlocacaoLocalUseCase alocacaoLocalUseCase) {
        this.eventoUseCase = eventoUseCase;
        this.alocacaoLocalUseCase = alocacaoLocalUseCase;
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return eventoUseCase.listar();
    }

    @GetMapping("/{id}")
    public EventoResponse buscar(@PathVariable String id) {
        return eventoUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoResponse criar(@RequestBody CriarEventoRequest request) {
        return eventoUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public EventoResponse editar(@PathVariable String id, @RequestBody EditarEventoRequest request) {
        return eventoUseCase.editar(id, request);
    }

    @PostMapping("/{id}/confirmar-preparacao")
    public EventoResponse confirmarPreparacao(@PathVariable String id) {
        return eventoUseCase.confirmarPreparacao(id);
    }

    @PostMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable String id) {
        eventoUseCase.cancelar(id);
    }

    @GetMapping("/{id}/locais/analise")
    public ResultadoAnaliseAlocacaoDto analisarLocais(
            @PathVariable String id,
            @RequestParam BigDecimal tetoCusto) {
        return alocacaoLocalUseCase.analisarLocais(id, tetoCusto);
    }

    @PostMapping("/{id}/parametros-alocacao")
    public EventoResponse registrarParametros(
            @PathVariable String id,
            @RequestBody RegistrarParametrosAlocacaoRequest request) {
        return alocacaoLocalUseCase.registrarParametros(id, request);
    }

    @PostMapping("/{id}/local-principal")
    public EventoResponse fixarLocalPrincipal(
            @PathVariable String id,
            @RequestBody FixarLocalPrincipalRequest request) {
        return alocacaoLocalUseCase.fixarLocalPrincipal(id, request);
    }

    @PostMapping("/{id}/alternativas-contingencia")
    public EventoResponse registrarAlternativas(
            @PathVariable String id,
            @RequestBody RegistrarAlternativasContingenciaRequest request) {
        return alocacaoLocalUseCase.registrarAlternativas(id, request);
    }

    @PostMapping("/{id}/definir-alocacao-local")
    public EventoResponse definirAlocacao(
            @PathVariable String id,
            @RequestBody DefinirAlocacaoLocalRequest request) {
        return alocacaoLocalUseCase.definirAlocacao(id, request);
    }

    @GetMapping("/{id}/risco-alocacao")
    public ResponseEntity<AlertaRiscoAlocacaoDto> avaliarRisco(@PathVariable String id) {
        return alocacaoLocalUseCase.avaliarRisco(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/troca-local-contingencia")
    public EventoResponse executarTrocaContingencia(
            @PathVariable String id,
            @RequestBody TrocaLocalContingenciaRequest request) {
        return alocacaoLocalUseCase.executarTrocaContingencia(id, request);
    }
}

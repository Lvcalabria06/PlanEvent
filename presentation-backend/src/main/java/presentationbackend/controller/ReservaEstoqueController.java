package presentationbackend.controller;

import application.estoque.dto.AtualizarReservaEstoqueRequest;
import application.estoque.dto.CriarReservaEstoqueRequest;
import application.estoque.dto.ReservaEstoqueResponse;
import application.estoque.usecase.ReservaEstoqueUseCase;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservas-estoque")
public class ReservaEstoqueController {

    private final ReservaEstoqueUseCase reservaEstoqueUseCase;

    public ReservaEstoqueController(ReservaEstoqueUseCase reservaEstoqueUseCase) {
        this.reservaEstoqueUseCase = reservaEstoqueUseCase;
    }

    @GetMapping
    public List<ReservaEstoqueResponse> listarTodas() {
        return reservaEstoqueUseCase.listarTodas();
    }

    @GetMapping("/por-evento/{eventoId}")
    public List<ReservaEstoqueResponse> listarPorEvento(@PathVariable String eventoId) {
        return reservaEstoqueUseCase.listarPorEvento(eventoId);
    }

    @GetMapping("/por-periodo")
    public List<ReservaEstoqueResponse> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return reservaEstoqueUseCase.listarPorPeriodo(inicio, fim);
    }

    @GetMapping("/{id}")
    public ReservaEstoqueResponse buscar(@PathVariable String id) {
        return reservaEstoqueUseCase.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaEstoqueResponse criar(@RequestBody CriarReservaEstoqueRequest request) {
        return reservaEstoqueUseCase.criar(request);
    }

    @PutMapping("/{id}")
    public ReservaEstoqueResponse editar(@PathVariable String id, @RequestBody AtualizarReservaEstoqueRequest request) {
        return reservaEstoqueUseCase.editar(id, request);
    }

    @PostMapping("/{id}/confirmar")
    public ReservaEstoqueResponse confirmar(@PathVariable String id) {
        return reservaEstoqueUseCase.confirmar(id);
    }

    @PostMapping("/{id}/iniciar-uso")
    public ReservaEstoqueResponse iniciarUso(@PathVariable String id) {
        return reservaEstoqueUseCase.iniciarUso(id);
    }

    @PostMapping("/{id}/finalizar")
    public ReservaEstoqueResponse finalizar(@PathVariable String id) {
        return reservaEstoqueUseCase.finalizar(id);
    }

    @PostMapping("/{id}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable String id) {
        reservaEstoqueUseCase.cancelar(id);
    }
}

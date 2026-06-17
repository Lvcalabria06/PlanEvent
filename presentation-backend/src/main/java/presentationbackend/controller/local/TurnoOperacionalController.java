package presentationbackend.controller.local;

import domain.local.turno.service.TurnoOperacionalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/locais/{localId}/turnos")
public class TurnoOperacionalController {

    private final TurnoOperacionalService turnoService;

    public TurnoOperacionalController(TurnoOperacionalService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public List<TurnoResponse> listarTurnos(@PathVariable String localId) {
        return turnoService.listarTurnosPorLocal(localId).stream()
                .map(TurnoResponse::de)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TurnoResponse cadastrarTurno(@PathVariable String localId,
                                        @RequestBody CadastrarTurnoRequest request) {
        return TurnoResponse.de(turnoService.cadastrarTurno(
                localId,
                request.nome(),
                LocalTime.parse(request.horaInicio()),
                LocalTime.parse(request.horaFim()),
                request.diasDaSemana(),
                request.capacidade(),
                request.observacoes()
        ));
    }

    @PutMapping("/{turnoId}")
    public TurnoResponse editarTurno(@PathVariable String localId,
                                     @PathVariable String turnoId,
                                     @RequestBody CadastrarTurnoRequest request) {
        return TurnoResponse.de(turnoService.editarTurno(
                turnoId,
                request.nome(),
                LocalTime.parse(request.horaInicio()),
                LocalTime.parse(request.horaFim()),
                request.diasDaSemana(),
                request.capacidade(),
                request.observacoes()
        ));
    }

    @PatchMapping("/{turnoId}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarTurno(@PathVariable String localId, @PathVariable String turnoId) {
        turnoService.desativarTurno(turnoId);
    }

    @DeleteMapping("/{turnoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerTurno(@PathVariable String localId, @PathVariable String turnoId) {
        turnoService.desativarTurno(turnoId);
    }
}

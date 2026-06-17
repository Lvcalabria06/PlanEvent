package presentationbackend.controller.local;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.service.AvaliacaoContextualLocalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locais/{localId}/avaliacoes")
public class AvaliacaoContextualController {

    private final AvaliacaoContextualLocalService avaliacaoService;

    public AvaliacaoContextualController(AvaliacaoContextualLocalService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoContextualResponse registrarAvaliacao(@PathVariable String localId,
                                                          @RequestBody RegistrarAvaliacaoRequest request) {
        return AvaliacaoContextualResponse.de(avaliacaoService.registrarAvaliacao(
                request.eventoId(),
                localId,
                request.notasPorCriterio(),
                request.justificativa(),
                request.usuarioResponsavel()
        ));
    }

    @GetMapping
    public List<AvaliacaoContextualResponse> listarHistorico(@PathVariable String localId) {
        return avaliacaoService.listarHistorico(localId).stream()
                .map(AvaliacaoContextualResponse::de)
                .toList();
    }

    @GetMapping("/resumo")
    public ResumoDesempenhoResponse consultarResumo(
            @PathVariable String localId,
            @RequestParam TipoEvento tipoEvento,
            @RequestParam PorteEvento porteEvento) {
        return ResumoDesempenhoResponse.de(
                avaliacaoService.consultarResumo(localId, tipoEvento, porteEvento));
    }
}

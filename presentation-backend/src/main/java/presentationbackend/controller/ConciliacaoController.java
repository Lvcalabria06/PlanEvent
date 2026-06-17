package presentationbackend.controller;

import application.conciliacao.dto.DespesaResumoResponse;
import application.conciliacao.dto.ExecutarConciliacaoRequest;
import application.conciliacao.dto.GerarRelatorioRequest;
import application.conciliacao.dto.RelatorioConciliacaoResponse;
import application.conciliacao.dto.VincularManualmenteRequest;
import application.conciliacao.dto.VinculoConciliacaoResponse;
import application.conciliacao.usecase.ConciliacaoUseCase;
import application.contrato.dto.ContratoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conciliacao")
public class ConciliacaoController {

    private final ConciliacaoUseCase conciliacaoUseCase;

    public ConciliacaoController(ConciliacaoUseCase conciliacaoUseCase) {
        this.conciliacaoUseCase = conciliacaoUseCase;
    }

    @PostMapping("/automatica")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executarAutomatica(@RequestBody ExecutarConciliacaoRequest request) {
        conciliacaoUseCase.executarConciliacaoAutomatica(request);
    }

    @PostMapping("/vincular")
    @ResponseStatus(HttpStatus.CREATED)
    public VinculoConciliacaoResponse vincularManualmente(@RequestBody VincularManualmenteRequest request) {
        return conciliacaoUseCase.vincularManualmente(request);
    }

    @GetMapping("/eventos/{eventoId}/despesas-descobertas")
    public List<DespesaResumoResponse> listarDespesasDescobertas(@PathVariable String eventoId) {
        return conciliacaoUseCase.listarDespesasDescobertasPorEvento(eventoId);
    }

    @GetMapping("/eventos/{eventoId}/contratos-extrapolados")
    public List<ContratoResponse> listarContratosExtrapolados(@PathVariable String eventoId) {
        return conciliacaoUseCase.listarContratosExtrapoladosPorEvento(eventoId);
    }

    @GetMapping("/eventos/{eventoId}/vinculos")
    public List<VinculoConciliacaoResponse> listarVinculos(@PathVariable String eventoId) {
        return conciliacaoUseCase.listarVinculosPorEvento(eventoId);
    }

    @PostMapping("/eventos/{eventoId}/relatorio")
    @ResponseStatus(HttpStatus.CREATED)
    public RelatorioConciliacaoResponse gerarRelatorio(@PathVariable String eventoId,
            @RequestBody GerarRelatorioRequest request) {
        return conciliacaoUseCase.gerarRelatorio(eventoId, request);
    }
}

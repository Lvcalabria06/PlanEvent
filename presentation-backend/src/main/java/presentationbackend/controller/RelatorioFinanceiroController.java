package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.usecase.RelatorioFinanceiroUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/financeiro/relatorios")
public class RelatorioFinanceiroController {

    private static final String USUARIO_PADRAO = "gestor@empresa.com";

    private final RelatorioFinanceiroUseCase relatorioUseCase;

    public RelatorioFinanceiroController(RelatorioFinanceiroUseCase relatorioUseCase) {
        this.relatorioUseCase = relatorioUseCase;
    }

    @GetMapping
    public List<FinanceiroDtos.RelatorioDto> listar(@PathVariable String eventoId) {
        return relatorioUseCase.listar(eventoId);
    }

    @GetMapping("/{relatorioId}")
    public FinanceiroDtos.RelatorioDto buscar(
            @PathVariable String eventoId,
            @PathVariable String relatorioId) {
        return relatorioUseCase.buscar(relatorioId);
    }

    @PostMapping("/simular")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.SimulacaoDto simular(
            @PathVariable String eventoId,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return relatorioUseCase.simular(eventoId, usuarioId);
    }

    /** Simulação what-if com parâmetros de cenário (RN15). */
    @PostMapping("/simular/what-if")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.SimulacaoDto simularWhatIf(
            @PathVariable String eventoId,
            @RequestBody FinanceiroDtos.SimularWhatIfRequest request,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return relatorioUseCase.simularWhatIf(eventoId, usuarioId, request);
    }

    @PostMapping("/simulacoes/{simulacaoId}/confirmar")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto confirmar(
            @PathVariable String eventoId,
            @PathVariable String simulacaoId,
            @RequestBody FinanceiroDtos.ConfirmarRelatorioRequest request) {
        return relatorioUseCase.confirmar(simulacaoId, request);
    }

    @PostMapping("/preliminar")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto gerarPreliminar(
            @PathVariable String eventoId,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return relatorioUseCase.gerarPreliminar(eventoId, usuarioId);
    }

    @PostMapping("/oficial")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto gerarOficial(
            @PathVariable String eventoId,
            @RequestBody(required = false) FinanceiroDtos.GerarOficialRequest request,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return relatorioUseCase.gerarOficial(eventoId, request, usuarioId);
    }

    /** Comparação entre dois snapshots escolhidos pelo usuário (RN17). */
    @GetMapping("/comparar")
    public FinanceiroDtos.ComparativoRelatorioParDto compararRelatorios(
            @PathVariable String eventoId,
            @org.springframework.web.bind.annotation.RequestParam String baseId,
            @org.springframework.web.bind.annotation.RequestParam String comparadoId) {
        return relatorioUseCase.compararRelatorios(baseId, comparadoId);
    }
}

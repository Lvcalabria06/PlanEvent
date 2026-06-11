package dev.proj.planevent.web;

import dev.proj.planevent.web.dto.FinanceiroDtos;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.service.RelatorioFinanceiroService;
import domain.financeiro.valueobject.TipoRelatorio;
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

    private final RelatorioFinanceiroService relatorioService;

    public RelatorioFinanceiroController(RelatorioFinanceiroService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping
    public List<FinanceiroDtos.RelatorioDto> listar(@PathVariable String eventoId) {
        return relatorioService.listarRelatoriosPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toRelatorioDto)
                .toList();
    }

    @GetMapping("/{relatorioId}")
    public FinanceiroDtos.RelatorioDto buscar(
            @PathVariable String eventoId,
            @PathVariable String relatorioId) {
        return FinanceiroMapper.toRelatorioDto(relatorioService.buscarRelatorio(relatorioId));
    }

    @PostMapping("/simular")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.SimulacaoDto simular(
            @PathVariable String eventoId,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        SimulacaoRelatorioFinanceiro simulacao = relatorioService.simularRelatorio(eventoId, usuarioId);
        return FinanceiroMapper.toSimulacaoDto(simulacao);
    }

    @PostMapping("/simulacoes/{simulacaoId}/confirmar")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto confirmar(
            @PathVariable String eventoId,
            @PathVariable String simulacaoId,
            @RequestBody FinanceiroDtos.ConfirmarRelatorioRequest request) {
        TipoRelatorio tipo = TipoRelatorio.valueOf(request.tipo());
        RelatorioFinanceiro relatorio = relatorioService.confirmarGeracao(
                simulacaoId, tipo, request.motivoNovaVersaoOficial());
        return FinanceiroMapper.toRelatorioDto(relatorio);
    }

    @PostMapping("/preliminar")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto gerarPreliminar(
            @PathVariable String eventoId,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return FinanceiroMapper.toRelatorioDto(
                relatorioService.gerarRelatorio(eventoId, usuarioId, TipoRelatorio.PRELIMINAR));
    }

    @PostMapping("/oficial")
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.RelatorioDto gerarOficial(
            @PathVariable String eventoId,
            @RequestBody(required = false) FinanceiroDtos.GerarOficialRequest request,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        String motivo = request != null ? request.motivoNovaVersaoOficial() : null;
        return FinanceiroMapper.toRelatorioDto(
                relatorioService.gerarRelatorioOficial(eventoId, usuarioId, motivo));
    }
}

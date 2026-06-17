package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.usecase.DespesaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos/{eventoId}/financeiro/despesas")
public class DespesaController {

    private static final String USUARIO_PADRAO = "gestor@empresa.com";

    private final DespesaUseCase despesaUseCase;

    public DespesaController(DespesaUseCase despesaUseCase) {
        this.despesaUseCase = despesaUseCase;
    }

    @GetMapping
    public List<FinanceiroDtos.DespesaDto> listar(
            @PathVariable String eventoId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fornecedorId) {
        return despesaUseCase.listar(eventoId, categoria, fornecedorId);
    }

    @GetMapping("/pendentes")
    public List<FinanceiroDtos.DespesaDto> listarPendentes(@PathVariable String eventoId) {
        return despesaUseCase.listarPendentes(eventoId);
    }

    @GetMapping("/desvios")
    public List<FinanceiroDtos.DesvioDto> desvios(@PathVariable String eventoId) {
        return despesaUseCase.desvios(eventoId);
    }

    @GetMapping("/{despesaId}")
    public FinanceiroDtos.DespesaDto buscar(@PathVariable String eventoId, @PathVariable String despesaId) {
        return despesaUseCase.buscar(despesaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.DespesaDto registrar(
            @PathVariable String eventoId,
            @RequestBody FinanceiroDtos.RegistrarDespesaRequest request,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        return despesaUseCase.registrar(eventoId, request, usuarioId);
    }

    @PutMapping("/{despesaId}")
    public FinanceiroDtos.DespesaDto atualizar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.AtualizarDespesaRequest request) {
        return despesaUseCase.atualizar(despesaId, request);
    }

    @DeleteMapping("/{despesaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String eventoId, @PathVariable String despesaId) {
        despesaUseCase.excluir(despesaId);
    }

    @PostMapping("/{despesaId}/aprovar")
    public FinanceiroDtos.DespesaDto aprovar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.AprovarDespesaRequest request) {
        return despesaUseCase.aprovar(despesaId, request);
    }

    @PostMapping("/{despesaId}/rejeitar")
    public FinanceiroDtos.DespesaDto rejeitar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.RejeitarDespesaRequest request) {
        return despesaUseCase.rejeitar(despesaId, request);
    }
}

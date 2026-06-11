package dev.proj.planevent.web;

import dev.proj.planevent.web.dto.FinanceiroDtos;
import domain.financeiro.entity.Despesa;
import domain.financeiro.service.DespesaService;
import domain.financeiro.valueobject.CategoriaDespesa;
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

    private final DespesaService despesaService;

    public DespesaController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    @GetMapping
    public List<FinanceiroDtos.DespesaDto> listar(
            @PathVariable String eventoId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fornecedorId) {
        if (categoria != null && !categoria.isBlank()) {
            return despesaService
                    .pesquisarPorCategoria(eventoId, CategoriaDespesa.valueOf(categoria))
                    .stream()
                    .map(FinanceiroMapper::toDespesaDto)
                    .toList();
        }
        if (fornecedorId != null && !fornecedorId.isBlank()) {
            return despesaService
                    .pesquisarPorFornecedor(eventoId, fornecedorId)
                    .stream()
                    .map(FinanceiroMapper::toDespesaDto)
                    .toList();
        }
        return despesaService.listarDespesasPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toDespesaDto)
                .toList();
    }

    @GetMapping("/pendentes")
    public List<FinanceiroDtos.DespesaDto> listarPendentes(@PathVariable String eventoId) {
        return despesaService.listarDespesasPorEvento(eventoId).stream()
                .filter(d -> d.getStatus().name().equals("PENDENTE_APROVACAO"))
                .map(FinanceiroMapper::toDespesaDto)
                .toList();
    }

    @GetMapping("/desvios")
    public List<FinanceiroDtos.DesvioDto> desvios(@PathVariable String eventoId) {
        return despesaService.calcularDesviosPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toDesvioDto)
                .toList();
    }

    @GetMapping("/{despesaId}")
    public FinanceiroDtos.DespesaDto buscar(@PathVariable String eventoId, @PathVariable String despesaId) {
        return FinanceiroMapper.toDespesaDto(despesaService.buscarDespesa(despesaId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.DespesaDto registrar(
            @PathVariable String eventoId,
            @RequestBody FinanceiroDtos.RegistrarDespesaRequest request,
            @RequestHeader(value = "X-Usuario-Id", defaultValue = USUARIO_PADRAO) String usuarioId) {
        Despesa despesa = new Despesa(
                eventoId,
                CategoriaDespesa.valueOf(request.categoria()),
                request.fornecedorId(),
                request.valor(),
                request.data(),
                usuarioId);
        return FinanceiroMapper.toDespesaDto(despesaService.registrarDespesa(despesa));
    }

    @PutMapping("/{despesaId}")
    public FinanceiroDtos.DespesaDto atualizar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.AtualizarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.atualizarDespesa(despesaId, request.valor(), request.data()));
    }

    @DeleteMapping("/{despesaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable String eventoId, @PathVariable String despesaId) {
        despesaService.excluirDespesa(despesaId);
    }

    @PostMapping("/{despesaId}/aprovar")
    public FinanceiroDtos.DespesaDto aprovar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.AprovarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.aprovarDespesa(despesaId, request.aprovadorId()));
    }

    @PostMapping("/{despesaId}/rejeitar")
    public FinanceiroDtos.DespesaDto rejeitar(
            @PathVariable String eventoId,
            @PathVariable String despesaId,
            @RequestBody FinanceiroDtos.RejeitarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.rejeitarDespesa(despesaId, request.aprovadorId(), request.motivo()));
    }
}

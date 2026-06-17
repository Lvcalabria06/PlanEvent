package presentationbackend.controller;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.usecase.AcaoPosRelatorioUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Gerencia ações pós-relatório derivadas de recomendações financeiras (RN18).
 * As ações são vinculadas ao relatório sem alterar seu snapshot imutável.
 * Base: /api/v1/financeiro/relatorios/{relatorioId}/acoes
 */
@RestController
@RequestMapping("/api/v1/financeiro/relatorios/{relatorioId}/acoes")
public class AcaoPosRelatorioController {

    private final AcaoPosRelatorioUseCase acaoUseCase;

    public AcaoPosRelatorioController(AcaoPosRelatorioUseCase acaoUseCase) {
        this.acaoUseCase = acaoUseCase;
    }

    @GetMapping
    public List<FinanceiroDtos.AcaoPosRelatorioDto> listar(@PathVariable String relatorioId) {
        return acaoUseCase.listarPorRelatorio(relatorioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinanceiroDtos.AcaoPosRelatorioDto registrar(
            @PathVariable String relatorioId,
            @RequestBody FinanceiroDtos.RegistrarAcaoPosRelatorioRequest request) {
        return acaoUseCase.registrar(relatorioId, request);
    }

    @PatchMapping("/{acaoId}/tratar")
    public FinanceiroDtos.AcaoPosRelatorioDto marcarComoTratada(@PathVariable String relatorioId,
                                                                  @PathVariable String acaoId) {
        return acaoUseCase.marcarComoTratada(acaoId);
    }
}

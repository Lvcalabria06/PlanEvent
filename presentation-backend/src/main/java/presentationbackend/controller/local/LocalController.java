package presentationbackend.controller.local;

import domain.local.service.LocalService;
import domain.local.service.ManutencaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para o CRUD de Locais e suas Manutenções.
 *
 * <p>Endpoints de Locais:</p>
 * <ul>
 *   <li>GET  /api/locais              — lista todos os locais (RN9)</li>
 *   <li>GET  /api/locais/{id}         — busca local por ID</li>
 *   <li>POST /api/locais              — cadastra novo local (RN1, RN2, RN3, RN4)</li>
 *   <li>PUT  /api/locais/{id}         — edita local existente (RN3, RN8)</li>
 *   <li>POST /api/locais/{id}/desativar — desativa local (RN6)</li>
 * </ul>
 *
 * <p>Endpoints de Manutenções:</p>
 * <ul>
 *   <li>GET    /api/locais/{id}/manutencoes                          — lista manutenções do local (RN7)</li>
 *   <li>POST   /api/locais/{id}/manutencoes                          — cadastra manutenção (RN1-RN5, RN8, RN9)</li>
 *   <li>PUT    /api/locais/{id}/manutencoes/{manutencaoId}           — edita manutenção (RN6, RN11)</li>
 *   <li>DELETE /api/locais/{id}/manutencoes/{manutencaoId}           — remove manutenção (RN10, RN12)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/locais")
public class LocalController {

    private final LocalService localService;
    private final ManutencaoService manutencaoService;

    public LocalController(LocalService localService, ManutencaoService manutencaoService) {
        this.localService = localService;
        this.manutencaoService = manutencaoService;
    }

    // ── LOCAIS ──────────────────────────────────────────────────────────────

    /** RN9: Visualizar todos os locais cadastrados. */
    @GetMapping
    public List<LocalResponse> listarLocais() {
        return localService.listarLocais().stream()
                .map(LocalResponse::de)
                .toList();
    }

    /**
     * Filtra locais usando uma expressão textual interpretada pelo padrão
     * Interpreter (GoF).
     *
     * <p>Campos suportados: {@code status}, {@code tipo},
     * {@code capacidade_min}, {@code capacidade_max}.<br>
     * Operadores lógicos: {@code AND}, {@code OR}.<br>
     * Agrupamento com parênteses é suportado.</p>
     *
     * <p>Exemplos:</p>
     * <pre>
     *   GET /api/locais/filtrar?q=status=ATIVO
     *   GET /api/locais/filtrar?q=tipo=Salão AND capacidade_min=100
     *   GET /api/locais/filtrar?q=(status=ATIVO OR status=EM_MANUTENCAO) AND capacidade_max=300
     * </pre>
     */
    @GetMapping("/filtrar")
    public List<LocalResponse> filtrarLocais(@RequestParam("q") String expressao) {
        return localService.filtrarLocais(expressao).stream()
                .map(LocalResponse::de)
                .toList();
    }

    /** Busca local por ID. */
    @GetMapping("/{id}")
    public LocalResponse buscarLocal(@PathVariable String id) {
        return localService.listarLocais().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .map(LocalResponse::de)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado: " + id));
    }

    /** RN1, RN2, RN3, RN4: Cadastra novo local com validações obrigatórias. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalResponse cadastrarLocal(@RequestBody CadastrarLocalRequest request) {
        return LocalResponse.de(localService.cadastrarLocal(
                request.nome(),
                request.capacidade(),
                request.endereco(),
                request.tipo(),
                request.infraestrutura(),
                request.custo()
        ));
    }

    /** RN3, RN8: Edita local e atualiza campo updatedAt. */
    @PutMapping("/{id}")
    public LocalResponse editarLocal(@PathVariable String id,
                                     @RequestBody EditarLocalRequest request) {
        return LocalResponse.de(localService.editarLocal(
                id,
                request.nome(),
                request.capacidade(),
                request.endereco(),
                request.tipo(),
                request.infraestrutura(),
                request.custo()
        ));
    }

    /** RN6: Desativação lógica do local (status INATIVO). */
    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativarLocal(@PathVariable String id) {
        localService.desativarLocal(id);
    }

    // ── MANUTENÇÕES ─────────────────────────────────────────────────────────

    /** RN7: Lista todas as manutenções de um local. */
    @GetMapping("/{id}/manutencoes")
    public List<ManutencaoResponse> listarManutencoes(@PathVariable String id) {
        return manutencaoService.listarManutencoesPorLocal(id).stream()
                .map(ManutencaoResponse::de)
                .toList();
    }

    /** RN1, RN2, RN3, RN5, RN8, RN9: Cadastra manutenção com validação de conflito. */
    @PostMapping("/{id}/manutencoes")
    @ResponseStatus(HttpStatus.CREATED)
    public ManutencaoResponse cadastrarManutencao(@PathVariable String id,
                                                  @RequestBody CadastrarManutencaoRequest request) {
        return ManutencaoResponse.de(manutencaoService.cadastrarManutencao(
                id,
                request.dataInicio(),
                request.dataFim(),
                request.responsavel()
        ));
    }

    /** RN6, RN11: Edita manutenção com revalidação de conflitos. */
    @PutMapping("/{id}/manutencoes/{manutencaoId}")
    public ManutencaoResponse editarManutencao(@PathVariable String id,
                                               @PathVariable String manutencaoId,
                                               @RequestBody EditarManutencaoRequest request) {
        return ManutencaoResponse.de(manutencaoService.editarManutencao(
                manutencaoId,
                request.dataInicio(),
                request.dataFim(),
                request.responsavel()
        ));
    }

    /** RN10, RN12: Remove manutenção considerando impacto em reservas futuras. */
    @DeleteMapping("/{id}/manutencoes/{manutencaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerManutencao(@PathVariable String id,
                                  @PathVariable String manutencaoId) {
        manutencaoService.removerManutencao(manutencaoId);
    }
}

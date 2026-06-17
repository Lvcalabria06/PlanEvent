package presentationbackend.controller;

import application.consulta.dto.EquipeResumoResponse;
import application.consulta.dto.MembroResumoResponse;
import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;
import domain.equipe.repository.EquipeRepository;
import domain.funcionario.repository.FuncionarioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de leitura usados pela camada de apresentação das tarefas para
 * popular seletores de equipe e responsáveis com dados reais (não mockados).
 *
 * Nota: GET /api/funcionarios foi movido para FuncionarioController.
 */
@RestController
@RequestMapping("/api")
public class ConsultaController {

    private final EquipeRepository equipeRepository;
    private final FuncionarioRepository funcionarioRepository;

    public ConsultaController(EquipeRepository equipeRepository,
            FuncionarioRepository funcionarioRepository) {
        this.equipeRepository = equipeRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping("/equipes")
    public List<EquipeResumoResponse> listarEquipes() {
        return equipeRepository.listarTodos().stream()
                .map(this::paraResumo)
                .toList();
    }

    private EquipeResumoResponse paraResumo(Equipe equipe) {
        List<MembroResumoResponse> membros = equipe.getMembros().stream()
                .map(this::paraMembroResumo)
                .toList();
        return new EquipeResumoResponse(equipe.getId(), equipe.getNome(), equipe.getEventoId(), membros);
    }

    private MembroResumoResponse paraMembroResumo(MembroEquipe membro) {
        String nome = funcionarioRepository.buscarPorId(membro.getFuncionarioId())
                .map(f -> f.getNome())
                .orElse(membro.getFuncionarioId());
        return new MembroResumoResponse(membro.getFuncionarioId(), nome, membro.isLider());
    }
}

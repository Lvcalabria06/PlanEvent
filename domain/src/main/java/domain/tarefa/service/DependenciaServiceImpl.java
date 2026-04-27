package domain.tarefa.service;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.TarefaRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DependenciaServiceImpl implements DependenciaService {

    private final TarefaRepository tarefaRepository;
    private final EquipeRepository equipeRepository;

    public DependenciaServiceImpl(TarefaRepository tarefaRepository, EquipeRepository equipeRepository) {
        this.tarefaRepository = tarefaRepository;
        this.equipeRepository = equipeRepository;
    }

    @Override
    public void adicionarDependencia(String tarefaId, String tarefaPredecessoraId) {
        if (tarefaId.equals(tarefaPredecessoraId)) {
            throw new IllegalArgumentException("Uma tarefa não pode depender de si mesma.");
        }

        Tarefa tarefa = buscarTarefa(tarefaId);
        Tarefa predecessora = buscarTarefa(tarefaPredecessoraId);

        // Bloqueio Inter-eventos: Validar se pertencem ao mesmo evento
        validarMesmoEvento(tarefa, predecessora);

        // Prevenção de Ciclos: Verificar se adicionar essa dependência cria um ciclo (DFS)
        if (existeCaminho(tarefaPredecessoraId, tarefaId)) {
            throw new IllegalStateException("A adição desta dependência criará um ciclo (Dependência Cíclica).");
        }

        // Consistência de Datas: A tarefa dependente não pode começar antes do fim da predecessora
        // Se a tarefa base já tiver data inicio e a predecessora tiver data fim
        if (tarefa.getDataInicio() != null && predecessora.getDataFim() != null) {
            if (tarefa.getDataInicio().isBefore(predecessora.getDataFim())) {
                throw new IllegalStateException("Data de início incompatível: a dependente inicia antes do término da predecessora.");
            }
        }

        tarefa.adicionarDependencia(tarefaPredecessoraId);
        tarefaRepository.salvar(tarefa);
    }

    @Override
    public void removerDependencia(String tarefaId, String tarefaPredecessoraId) {
        Tarefa tarefa = buscarTarefa(tarefaId);
        tarefa.removerDependencia(tarefaPredecessoraId);
        tarefaRepository.salvar(tarefa);
    }

    @Override
    public List<Tarefa> listarDependencias(String tarefaId) {
        Tarefa tarefa = buscarTarefa(tarefaId);
        List<String> ids = tarefa.listarDependencias();
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        return tarefaRepository.listarPorIds(ids);
    }

    @Override
    public List<Tarefa> listarDependentes(String tarefaId) {
        return tarefaRepository.listarDependentes(tarefaId);
    }

    @Override
    public boolean possuiImpactoAtraso(String tarefaPredecessoraId) {
        Tarefa predecessora = buscarTarefa(tarefaPredecessoraId);
        if (predecessora.getDataFim() == null) return false;

        List<Tarefa> dependentes = listarDependentes(tarefaPredecessoraId);
        for (Tarefa dep : dependentes) {
            if (dep.getDataInicio() != null && dep.getDataInicio().isBefore(predecessora.getDataFim())) {
                return true;
            }
        }
        return false;
    }

    // --- Métodos Auxiliares ---

    private Tarefa buscarTarefa(String id) {
        return tarefaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + id));
    }

    private void validarMesmoEvento(Tarefa t1, Tarefa t2) {
        Equipe e1 = equipeRepository.buscarPorId(t1.getEquipeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipe da tarefa primária não encontrada."));
        Equipe e2 = equipeRepository.buscarPorId(t2.getEquipeId())
                .orElseThrow(() -> new IllegalArgumentException("Equipe da tarefa predecessora não encontrada."));

        if (!e1.getEventoId().equals(e2.getEventoId())) {
            throw new IllegalStateException("Não é possível criar dependência entre tarefas de eventos diferentes.");
        }
    }

    /**
     * Verifica se existe um caminho direcionado de 'startId' para 'targetId'.
     * Usado para evitar ciclos: se B (start) já chega em A (target), A não pode depender de B.
     */
    private boolean existeCaminho(String startId, String targetId) {
        return dfs(startId, targetId, new HashSet<>());
    }

    private boolean dfs(String currentId, String targetId, Set<String> visitados) {
        if (currentId.equals(targetId)) {
            return true;
        }
        visitados.add(currentId);

        Optional<Tarefa> tarefaOpt = tarefaRepository.buscarPorId(currentId);
        if (tarefaOpt.isEmpty()) {
            return false;
        }

        List<String> dependencias = tarefaOpt.get().listarDependencias();
        for (String proximoId : dependencias) {
            if (!visitados.contains(proximoId)) {
                if (dfs(proximoId, targetId, visitados)) {
                    return true;
                }
            }
        }
        return false;
    }
}

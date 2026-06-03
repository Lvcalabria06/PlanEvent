package application.dependencia.usecase;

import application.dependencia.dto.AdicionarDependenciaRequest;
import application.tarefa.dto.TarefaResponse;
import application.tarefa.mapper.TarefaDtoMapper;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.service.DependenciaService;
import domain.tarefa.service.TarefaService;

import java.util.List;

public class DependenciaUseCaseImpl implements DependenciaUseCase {

    private final DependenciaService dependenciaService;
    private final TarefaService tarefaService;

    public DependenciaUseCaseImpl(DependenciaService dependenciaService, TarefaService tarefaService) {
        this.dependenciaService = dependenciaService;
        this.tarefaService = tarefaService;
    }

    @Override
    public void adicionar(String tarefaId, AdicionarDependenciaRequest request) {
        dependenciaService.adicionarDependencia(tarefaId, request.tarefaPredecessoraId());
    }

    @Override
    public void remover(String tarefaId, String tarefaPredecessoraId) {
        dependenciaService.removerDependencia(tarefaId, tarefaPredecessoraId);
    }

    @Override
    public List<TarefaResponse> listarDependencias(String tarefaId) {
        return dependenciaService.listarDependencias(tarefaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public List<TarefaResponse> listarDependentes(String tarefaId) {
        return dependenciaService.listarDependentes(tarefaId).stream()
                .map(this::paraResposta)
                .toList();
    }

    private TarefaResponse paraResposta(Tarefa tarefa) {
        List<String> responsaveis = tarefaService.listarResponsaveis(tarefa.getId());
        return TarefaDtoMapper.paraResposta(tarefa, responsaveis);
    }
}

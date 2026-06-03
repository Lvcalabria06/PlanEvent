package application.tarefa.usecase;

import application.tarefa.dto.AtribuirResponsavelRequest;
import application.tarefa.dto.CriarTarefaRequest;
import application.tarefa.dto.EditarTarefaRequest;
import application.tarefa.dto.TarefaResponse;
import application.tarefa.mapper.TarefaDtoMapper;
import domain.tarefa.entity.Tarefa;
import domain.tarefa.service.TarefaService;

import java.util.List;

public class TarefaUseCaseImpl implements TarefaUseCase {

    private final TarefaService tarefaService;

    public TarefaUseCaseImpl(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @Override
    public TarefaResponse criar(CriarTarefaRequest request) {
        Tarefa novaTarefa = new Tarefa(
                request.equipeId(),
                request.titulo(),
                request.descricao(),
                request.dataInicio(),
                request.dataFim());
        Tarefa criada = tarefaService.criarTarefa(novaTarefa);
        return paraResposta(criada);
    }

    @Override
    public TarefaResponse editar(String tarefaId, EditarTarefaRequest request) {
        Tarefa editada = tarefaService.editarTarefa(
                tarefaId,
                request.titulo(),
                request.descricao(),
                request.dataInicio(),
                request.dataFim());
        return paraResposta(editada);
    }

    @Override
    public void remover(String tarefaId) {
        tarefaService.removerTarefa(tarefaId);
    }

    @Override
    public void iniciar(String tarefaId) {
        tarefaService.iniciarTarefa(tarefaId);
    }

    @Override
    public void concluir(String tarefaId) {
        tarefaService.concluirTarefa(tarefaId);
    }

    @Override
    public void atribuirResponsavel(String tarefaId, AtribuirResponsavelRequest request) {
        tarefaService.atribuirResponsavel(tarefaId, request.funcionarioId());
    }

    @Override
    public List<TarefaResponse> listarPorEquipe(String equipeId) {
        return tarefaService.listarPorEquipe(equipeId).stream()
                .map(this::paraResposta)
                .toList();
    }

    @Override
    public List<TarefaResponse> listarPorEvento(String eventoId) {
        return tarefaService.listarPorEvento(eventoId).stream()
                .map(this::paraResposta)
                .toList();
    }

    private TarefaResponse paraResposta(Tarefa tarefa) {
        List<String> responsaveis = tarefaService.listarResponsaveis(tarefa.getId());
        return TarefaDtoMapper.paraResposta(tarefa, responsaveis);
    }
}

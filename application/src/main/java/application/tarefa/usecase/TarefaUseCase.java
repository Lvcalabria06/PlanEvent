package application.tarefa.usecase;

import application.tarefa.dto.AtribuirResponsavelRequest;
import application.tarefa.dto.CriarTarefaRequest;
import application.tarefa.dto.EditarTarefaRequest;
import application.tarefa.dto.TarefaResponse;

import java.util.List;

/**
 * Casos de uso de gerenciamento de tarefas (Funcionalidade 5 - Passo 1),
 * orquestrando o serviço de domínio e expondo DTOs à camada de apresentação.
 */
public interface TarefaUseCase {

    TarefaResponse criar(CriarTarefaRequest request);

    TarefaResponse editar(String tarefaId, EditarTarefaRequest request);

    void remover(String tarefaId);

    void iniciar(String tarefaId);

    void concluir(String tarefaId);

    void atribuirResponsavel(String tarefaId, AtribuirResponsavelRequest request);

    void removerResponsavel(String tarefaId, String funcionarioId);

    List<TarefaResponse> listarPorEquipe(String equipeId);

    List<TarefaResponse> listarPorEvento(String eventoId);

    List<TarefaResponse> listarTodas();
}

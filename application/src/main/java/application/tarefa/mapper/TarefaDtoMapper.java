package application.tarefa.mapper;

import application.tarefa.dto.TarefaResponse;
import domain.tarefa.entity.Tarefa;

import java.util.List;

/**
 * Converte a entidade de domínio {@link Tarefa} para o DTO de saída
 * {@link TarefaResponse}, evitando expor o modelo de domínio na borda web.
 */
public final class TarefaDtoMapper {

    private TarefaDtoMapper() {
    }

    public static TarefaResponse paraResposta(Tarefa tarefa, List<String> responsaveis) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getEquipeId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getDataInicio(),
                tarefa.getDataFim(),
                tarefa.getStatus() != null ? tarefa.getStatus().name() : null,
                tarefa.listarDependencias(),
                responsaveis != null ? responsaveis : List.of(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao());
    }
}

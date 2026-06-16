package infrastructure.persistence.tarefa.mapper;

import domain.tarefa.entity.Tarefa;
import infrastructure.persistence.tarefa.entity.TarefaJpaEntity;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Converte entre a entidade de domínio {@link Tarefa} e seu mapeamento
 * relacional {@link TarefaJpaEntity}.
 */
public final class TarefaMapper {

    private TarefaMapper() {
    }

    public static TarefaJpaEntity paraJpa(Tarefa tarefa) {
        return new TarefaJpaEntity(
                tarefa.getId(),
                tarefa.getEquipeId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getDataInicio(),
                tarefa.getDataFim(),
                tarefa.getStatus(),
                new HashSet<>(tarefa.listarDependencias()),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao());
    }

    public static Tarefa paraDominio(TarefaJpaEntity entity) {
        return Tarefa.reconstituir(
                entity.getId(),
                entity.getEquipeId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                new ArrayList<>(entity.getDependenciasIds()),
                entity.getDataCriacao(),
                entity.getDataAtualizacao());
    }
}

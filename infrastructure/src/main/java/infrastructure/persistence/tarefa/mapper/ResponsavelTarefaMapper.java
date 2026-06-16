package infrastructure.persistence.tarefa.mapper;

import domain.tarefa.entity.ResponsavelTarefa;
import infrastructure.persistence.tarefa.entity.ResponsavelTarefaJpaEntity;

/**
 * Converte entre {@link ResponsavelTarefa} (domínio) e
 * {@link ResponsavelTarefaJpaEntity} (relacional).
 */
public final class ResponsavelTarefaMapper {

    private ResponsavelTarefaMapper() {
    }

    public static ResponsavelTarefaJpaEntity paraJpa(ResponsavelTarefa responsavel) {
        return new ResponsavelTarefaJpaEntity(
                responsavel.getId(),
                responsavel.getTarefaId(),
                responsavel.getFuncionarioId());
    }

    public static ResponsavelTarefa paraDominio(ResponsavelTarefaJpaEntity entity) {
        return ResponsavelTarefa.reconstituir(
                entity.getId(),
                entity.getTarefaId(),
                entity.getFuncionarioId());
    }
}

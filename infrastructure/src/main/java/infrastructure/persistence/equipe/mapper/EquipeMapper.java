package infrastructure.persistence.equipe.mapper;

import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;
import infrastructure.persistence.equipe.entity.EquipeJpaEntity;
import infrastructure.persistence.equipe.entity.MembroEquipeJpaEntity;

import java.util.List;

public final class EquipeMapper {

    private EquipeMapper() {
    }

    public static EquipeJpaEntity paraJpa(Equipe equipe) {
        if (equipe == null) {
            return null;
        }

        EquipeJpaEntity entity = new EquipeJpaEntity(
                equipe.getId(),
                equipe.getEventoId(),
                equipe.getNome(),
                equipe.getDataCriacao(),
                equipe.getDataAtualizacao()
        );

        if (equipe.getMembros() != null) {
            List<MembroEquipeJpaEntity> membroEntities = equipe.getMembros().stream()
                    .map(m -> new MembroEquipeJpaEntity(
                            m.getId(),
                            m.getFuncionarioId(),
                            m.isLider(),
                            m.getDataEntrada(),
                            entity
                    ))
                    .toList();
            entity.setMembros(membroEntities);
        }

        return entity;
    }

    public static Equipe paraDominio(EquipeJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        List<MembroEquipe> membros = List.of();
        if (entity.getMembros() != null) {
            membros = entity.getMembros().stream()
                    .map(m -> MembroEquipe.reconstituir(
                            m.getId(),
                            m.getFuncionarioId(),
                            m.isLider(),
                            m.getDataEntrada()
                    ))
                    .toList();
        }

        return Equipe.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getNome(),
                membros,
                entity.getDataCriacao(),
                entity.getDataAtualizacao()
        );
    }
}

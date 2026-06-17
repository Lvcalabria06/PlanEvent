package application.equipe.mapper;

import application.equipe.dto.EquipeResponse;
import application.equipe.dto.MembroEquipeResponse;
import domain.equipe.entity.Equipe;
import domain.equipe.entity.MembroEquipe;

import java.util.List;

/**
 * Converte a entidade de domínio {@link Equipe} para o DTO de saída {@link EquipeResponse}.
 */
public final class EquipeDtoMapper {

    private EquipeDtoMapper() {
    }

    public static EquipeResponse paraResposta(Equipe equipe) {
        List<MembroEquipeResponse> membros = equipe.getMembros().stream()
                .map(EquipeDtoMapper::paraMembroResposta)
                .toList();

        return new EquipeResponse(
                equipe.getId(),
                equipe.getEventoId(),
                equipe.getNome(),
                membros,
                equipe.getDataCriacao(),
                equipe.getDataAtualizacao());
    }

    private static MembroEquipeResponse paraMembroResposta(MembroEquipe membro) {
        return new MembroEquipeResponse(
                membro.getId(),
                membro.getFuncionarioId(),
                membro.isLider(),
                membro.getDataEntrada());
    }
}

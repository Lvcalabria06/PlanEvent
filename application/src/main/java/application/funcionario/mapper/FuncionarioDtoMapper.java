package application.funcionario.mapper;

import application.funcionario.dto.FuncionarioResponse;
import domain.funcionario.entity.Funcionario;

/**
 * Converte a entidade de domínio {@link Funcionario} para o DTO de saída {@link FuncionarioResponse}.
 */
public final class FuncionarioDtoMapper {

    private FuncionarioDtoMapper() {
    }

    public static FuncionarioResponse paraResposta(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo() != null ? funcionario.getCargo().name() : null,
                funcionario.getDisponibilidade() != null ? funcionario.getDisponibilidade().name() : null,
                funcionario.isAtivo(),
                funcionario.getCompetencias(),
                funcionario.getCreatedAt(),
                funcionario.getUpdatedAt());
    }
}

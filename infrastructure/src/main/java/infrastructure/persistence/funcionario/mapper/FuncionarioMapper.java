package infrastructure.persistence.funcionario.mapper;

import domain.funcionario.entity.Funcionario;
import infrastructure.persistence.funcionario.entity.FuncionarioJpaEntity;

/**
 * Converte entre a entidade de domínio {@link Funcionario} e seu mapeamento
 * relacional {@link FuncionarioJpaEntity}.
 */
public final class FuncionarioMapper {

    private FuncionarioMapper() {
    }

    public static FuncionarioJpaEntity paraJpa(Funcionario funcionario) {
        return new FuncionarioJpaEntity(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().name(),
                funcionario.getDisponibilidade().name(),
                funcionario.isAtivo(),
                funcionario.getCreatedAt(),
                funcionario.getUpdatedAt());
    }

    public static Funcionario paraDominio(FuncionarioJpaEntity entity) {
        return Funcionario.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getCargo(),
                entity.getDisponibilidade(),
                entity.isAtivo(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

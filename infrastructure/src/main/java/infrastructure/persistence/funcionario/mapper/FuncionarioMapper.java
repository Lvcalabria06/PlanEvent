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
        String compsString = funcionario.getCompetencias() != null ? String.join(",", funcionario.getCompetencias()) : "";
        return new FuncionarioJpaEntity(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().name(),
                funcionario.getDisponibilidade().name(),
                funcionario.isAtivo(),
                compsString,
                funcionario.getCreatedAt(),
                funcionario.getUpdatedAt());
    }

    public static Funcionario paraDominio(FuncionarioJpaEntity entity) {
        java.util.List<String> compsList = new java.util.ArrayList<>();
        if (entity.getCompetencias() != null && !entity.getCompetencias().trim().isEmpty()) {
            for (String c : entity.getCompetencias().split(",")) {
                String trimmed = c.trim();
                if (!trimmed.isEmpty()) {
                    compsList.add(trimmed);
                }
            }
        }
        return Funcionario.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getCargo(),
                entity.getDisponibilidade(),
                entity.isAtivo(),
                compsList,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}

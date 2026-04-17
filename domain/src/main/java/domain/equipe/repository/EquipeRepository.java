package domain.equipe.repository;

import java.util.Optional;

import domain.equipe.entity.Equipe;

public interface EquipeRepository {
    Optional<Equipe> buscarPorId(String id);
}

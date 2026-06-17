package infrastructure.persistence.local.repository;

import infrastructure.persistence.local.entity.AvaliacaoContextualLocalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoContextualLocalJpaRepository extends JpaRepository<AvaliacaoContextualLocalJpaEntity, String> {
    List<AvaliacaoContextualLocalJpaEntity> findByLocalId(String localId);
    boolean existsByEventoIdAndLocalId(String eventoId, String localId);
}

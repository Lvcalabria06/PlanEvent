package infrastructure.persistence.local.repository;

import infrastructure.persistence.local.entity.ManutencaoLocalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManutencaoLocalJpaRepository extends JpaRepository<ManutencaoLocalJpaEntity, String> {
    List<ManutencaoLocalJpaEntity> findByLocalId(String localId);
}

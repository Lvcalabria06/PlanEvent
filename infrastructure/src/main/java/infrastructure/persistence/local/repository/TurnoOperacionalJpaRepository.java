package infrastructure.persistence.local.repository;

import infrastructure.persistence.local.entity.TurnoOperacionalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurnoOperacionalJpaRepository extends JpaRepository<TurnoOperacionalJpaEntity, String> {
    List<TurnoOperacionalJpaEntity> findByLocalId(String localId);
}

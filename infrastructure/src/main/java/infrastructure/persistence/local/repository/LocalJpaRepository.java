package infrastructure.persistence.local.repository;

import infrastructure.persistence.local.entity.LocalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalJpaRepository extends JpaRepository<LocalJpaEntity, String> {
}

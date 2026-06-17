package infrastructure.persistence.estoque.repository;

import infrastructure.persistence.estoque.entity.PrevisaoConsumoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrevisaoConsumoJpaRepository extends JpaRepository<PrevisaoConsumoJpaEntity, String> {

    Optional<PrevisaoConsumoJpaEntity> findByEventoId(String eventoId);
}

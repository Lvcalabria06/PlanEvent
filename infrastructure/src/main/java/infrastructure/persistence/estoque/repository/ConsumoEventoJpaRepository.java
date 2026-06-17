package infrastructure.persistence.estoque.repository;

import infrastructure.persistence.estoque.entity.ConsumoEventoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumoEventoJpaRepository extends JpaRepository<ConsumoEventoJpaEntity, String> {

    List<ConsumoEventoJpaEntity> findByEventoId(String eventoId);
}

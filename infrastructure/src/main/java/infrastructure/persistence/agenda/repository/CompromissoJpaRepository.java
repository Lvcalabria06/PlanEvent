package infrastructure.persistence.agenda.repository;

import infrastructure.persistence.agenda.entity.CompromissoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompromissoJpaRepository extends JpaRepository<CompromissoJpaEntity, String> {

    List<CompromissoJpaEntity> findByGestorId(String gestorId);
}

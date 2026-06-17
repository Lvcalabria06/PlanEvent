package infrastructure.persistence.estoque.repository;

import domain.estoque.valueobject.StatusRedistribuicao;
import infrastructure.persistence.estoque.entity.CenarioRedistribuicaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CenarioRedistribuicaoJpaRepository extends JpaRepository<CenarioRedistribuicaoJpaEntity, String> {

    List<CenarioRedistribuicaoJpaEntity> findByStatus(StatusRedistribuicao status);
}

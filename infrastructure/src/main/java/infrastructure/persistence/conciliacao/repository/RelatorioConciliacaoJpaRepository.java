package infrastructure.persistence.conciliacao.repository;

import infrastructure.persistence.conciliacao.entity.RelatorioConciliacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatorioConciliacaoJpaRepository extends JpaRepository<RelatorioConciliacaoJpaEntity, String> {}

package infrastructure.persistence.conciliacao.repository;

import infrastructure.persistence.conciliacao.entity.VinculoConciliacaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VinculoConciliacaoJpaRepository extends JpaRepository<VinculoConciliacaoJpaEntity, String> {

    Optional<VinculoConciliacaoJpaEntity> findByDespesaId(String despesaId);

    List<VinculoConciliacaoJpaEntity> findByEventoId(String eventoId);

    List<VinculoConciliacaoJpaEntity> findByContratoId(String contratoId);

    void deleteByDespesaId(String despesaId);
}

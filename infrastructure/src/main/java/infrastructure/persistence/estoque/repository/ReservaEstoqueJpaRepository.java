package infrastructure.persistence.estoque.repository;

import infrastructure.persistence.estoque.entity.ReservaEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaEstoqueJpaRepository extends JpaRepository<ReservaEstoqueJpaEntity, String> {

    List<ReservaEstoqueJpaEntity> findByEventoId(String eventoId);

    @Query("SELECT r FROM ReservaEstoqueJpaEntity r WHERE r.dataInicio <= :fim AND r.dataFim >= :inicio")
    List<ReservaEstoqueJpaEntity> findByPeriodoOverlap(@Param("inicio") LocalDateTime inicio,
                                                       @Param("fim") LocalDateTime fim);
}

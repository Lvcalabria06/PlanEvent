package infrastructure.persistence.agenda.repository;

import infrastructure.persistence.agenda.entity.LembreteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface LembreteJpaRepository extends JpaRepository<LembreteJpaEntity, String> {

    List<LembreteJpaEntity> findByCompromissoId(String compromissoId);

    List<LembreteJpaEntity> findByEventoId(String eventoId);

    List<LembreteJpaEntity> findByNotificadoFalseAndHorarioLessThanEqual(LocalDateTime horario);

    @Modifying
    @Transactional
    @Query("DELETE FROM LembreteJpaEntity l WHERE l.compromissoId = :compromissoId")
    void deleteByCompromissoId(@Param("compromissoId") String compromissoId);
}

package infrastructure.persistence.equipe.repository;

import infrastructure.persistence.equipe.entity.EquipeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipeJpaRepository extends JpaRepository<EquipeJpaEntity, String> {

    List<EquipeJpaEntity> findByEventoId(String eventoId);

    boolean existsByEventoIdAndNome(String eventoId, String nome);

    @Query("SELECT COUNT(e) > 0 FROM EquipeJpaEntity e JOIN e.membros m WHERE m.funcionarioId = :funcionarioId AND e.eventoId = :eventoId")
    boolean funcionarioJaEstaEmEquipeNoEvento(@Param("funcionarioId") String funcionarioId, @Param("eventoId") String eventoId);

    @Query("SELECT COUNT(e) > 0 FROM EquipeJpaEntity e JOIN e.membros m WHERE m.funcionarioId = :funcionarioId")
    boolean existeFuncionarioVinculado(@Param("funcionarioId") String funcionarioId);
}

package infrastructure.persistence.tarefa.repository;

import infrastructure.persistence.tarefa.entity.TarefaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaJpaRepository extends JpaRepository<TarefaJpaEntity, String> {

    List<TarefaJpaEntity> findByEquipeId(String equipeId);

    boolean existsByTituloAndEquipeId(String titulo, String equipeId);

    List<TarefaJpaEntity> findByIdIn(List<String> ids);

    /**
     * Tarefas que possuem a tarefa informada como predecessora (dependentes diretos).
     */
    @Query("SELECT t FROM TarefaJpaEntity t JOIN t.dependenciasIds d WHERE d = :tarefaId")
    List<TarefaJpaEntity> findDependentes(@Param("tarefaId") String tarefaId);
}

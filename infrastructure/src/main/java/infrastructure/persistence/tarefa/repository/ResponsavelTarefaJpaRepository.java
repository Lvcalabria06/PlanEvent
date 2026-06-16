package infrastructure.persistence.tarefa.repository;

import infrastructure.persistence.tarefa.entity.ResponsavelTarefaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsavelTarefaJpaRepository extends JpaRepository<ResponsavelTarefaJpaEntity, String> {

    List<ResponsavelTarefaJpaEntity> findByTarefaId(String tarefaId);

    boolean existsByTarefaIdAndFuncionarioId(String tarefaId, String funcionarioId);
}

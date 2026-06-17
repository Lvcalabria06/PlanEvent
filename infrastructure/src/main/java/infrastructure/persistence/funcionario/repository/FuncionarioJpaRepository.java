package infrastructure.persistence.funcionario.repository;

import infrastructure.persistence.funcionario.entity.FuncionarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório Spring Data JPA para Funcionário.
 */
public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioJpaEntity, String> {

    List<FuncionarioJpaEntity> findByAtivoTrue();
}

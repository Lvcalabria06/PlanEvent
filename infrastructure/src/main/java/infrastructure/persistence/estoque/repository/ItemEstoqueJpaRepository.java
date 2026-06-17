package infrastructure.persistence.estoque.repository;

import infrastructure.persistence.estoque.entity.ItemEstoqueJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemEstoqueJpaRepository extends JpaRepository<ItemEstoqueJpaEntity, String> {

    List<ItemEstoqueJpaEntity> findByAtivoTrue();
}

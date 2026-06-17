package infrastructure.persistence.estoque.repository;

import infrastructure.persistence.estoque.entity.ItemSubstituicaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemSubstituicaoJpaRepository extends JpaRepository<ItemSubstituicaoJpaEntity, String> {

    List<ItemSubstituicaoJpaEntity> findByItemOriginalId(String itemOriginalId);
}

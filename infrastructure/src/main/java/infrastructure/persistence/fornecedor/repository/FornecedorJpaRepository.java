package infrastructure.persistence.fornecedor.repository;

import infrastructure.persistence.fornecedor.entity.FornecedorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FornecedorJpaRepository extends JpaRepository<FornecedorJpaEntity, String> {

    Optional<FornecedorJpaEntity> findByCnpj(String cnpj);
}

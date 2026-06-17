package infrastructure.persistence.contrato.repository;

import infrastructure.persistence.contrato.entity.ContratoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoJpaRepository extends JpaRepository<ContratoJpaEntity, String> {

    List<ContratoJpaEntity> findByEventoId(String eventoId);

    List<ContratoJpaEntity> findByFornecedorId(String fornecedorId);
}

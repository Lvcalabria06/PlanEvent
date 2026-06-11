package infrastructure.persistence.contrato.repository;

import domain.contrato.valueobject.StatusContrato;
import infrastructure.persistence.contrato.entity.ContratoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoJpaRepository extends JpaRepository<ContratoJpaEntity, String> {

    List<ContratoJpaEntity> findByEventoId(String eventoId);

    boolean existsByFornecedorIdAndStatusNotIn(String fornecedorId, List<StatusContrato> statusExcluidos);
}

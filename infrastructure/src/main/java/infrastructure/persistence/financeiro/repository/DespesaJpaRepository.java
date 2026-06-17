package infrastructure.persistence.financeiro.repository;

import domain.financeiro.valueobject.CategoriaDespesa;
import infrastructure.persistence.financeiro.entity.DespesaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DespesaJpaRepository extends JpaRepository<DespesaJpaEntity, String> {
    List<DespesaJpaEntity> findByEventoId(String eventoId);
    List<DespesaJpaEntity> findByEventoIdAndCategoria(String eventoId, CategoriaDespesa categoria);
    List<DespesaJpaEntity> findByEventoIdAndFornecedorId(String eventoId, String fornecedorId);
    List<DespesaJpaEntity> findByFornecedorId(String fornecedorId);
}

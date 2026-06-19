package infrastructure.persistence.financeiro.repository;

import infrastructure.persistence.financeiro.entity.RelatorioFinanceiroJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelatorioFinanceiroJpaRepository extends JpaRepository<RelatorioFinanceiroJpaEntity, String> {
    List<RelatorioFinanceiroJpaEntity> findByEventoIdOrderByDataGeracaoAsc(String eventoId);
}

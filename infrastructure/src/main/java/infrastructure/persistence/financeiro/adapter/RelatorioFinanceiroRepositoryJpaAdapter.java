package infrastructure.persistence.financeiro.adapter;

import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import infrastructure.persistence.financeiro.mapper.RelatorioFinanceiroMapper;
import infrastructure.persistence.financeiro.repository.RelatorioFinanceiroJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RelatorioFinanceiroRepositoryJpaAdapter implements RelatorioFinanceiroRepository {

    private final RelatorioFinanceiroJpaRepository jpaRepository;

    public RelatorioFinanceiroRepositoryJpaAdapter(RelatorioFinanceiroJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RelatorioFinanceiro salvar(RelatorioFinanceiro relatorio) {
        return RelatorioFinanceiroMapper.paraDominio(jpaRepository.save(RelatorioFinanceiroMapper.paraJpa(relatorio)));
    }

    @Override
    public Optional<RelatorioFinanceiro> buscarPorId(String id) {
        return jpaRepository.findById(id).map(RelatorioFinanceiroMapper::paraDominio);
    }

    @Override
    public List<RelatorioFinanceiro> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoIdOrderByDataGeracaoAsc(eventoId).stream()
                .map(RelatorioFinanceiroMapper::paraDominio)
                .toList();
    }
}

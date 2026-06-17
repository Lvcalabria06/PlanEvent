package infrastructure.persistence.estoque.adapter;

import domain.estoque.entity.CenarioRedistribuicao;
import domain.estoque.repository.CenarioRedistribuicaoRepository;
import domain.estoque.valueobject.StatusRedistribuicao;
import infrastructure.persistence.estoque.mapper.CenarioRedistribuicaoMapper;
import infrastructure.persistence.estoque.repository.CenarioRedistribuicaoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CenarioRedistribuicaoRepositoryJpaAdapter implements CenarioRedistribuicaoRepository {

    private final CenarioRedistribuicaoJpaRepository jpaRepository;

    public CenarioRedistribuicaoRepositoryJpaAdapter(CenarioRedistribuicaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CenarioRedistribuicao salvar(CenarioRedistribuicao cenario) {
        return CenarioRedistribuicaoMapper.paraDominio(jpaRepository.save(CenarioRedistribuicaoMapper.paraJpa(cenario)));
    }

    @Override
    public Optional<CenarioRedistribuicao> buscarPorId(String id) {
        return jpaRepository.findById(id).map(CenarioRedistribuicaoMapper::paraDominio);
    }

    @Override
    public List<CenarioRedistribuicao> listarPendentes() {
        return jpaRepository.findByStatus(StatusRedistribuicao.PENDENTE).stream()
                .map(CenarioRedistribuicaoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<CenarioRedistribuicao> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(CenarioRedistribuicaoMapper::paraDominio)
                .toList();
    }
}

package infrastructure.persistence.conciliacao.adapter;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.repository.RelatorioConciliacaoRepository;
import infrastructure.persistence.conciliacao.mapper.RelatorioConciliacaoMapper;
import infrastructure.persistence.conciliacao.repository.RelatorioConciliacaoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class RelatorioConciliacaoRepositoryJpaAdapter implements RelatorioConciliacaoRepository {

    private final RelatorioConciliacaoJpaRepository jpaRepository;

    public RelatorioConciliacaoRepositoryJpaAdapter(RelatorioConciliacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public RelatorioConciliacao salvar(RelatorioConciliacao relatorio) {
        return RelatorioConciliacaoMapper.paraDominio(
                jpaRepository.save(RelatorioConciliacaoMapper.paraJpa(relatorio)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioConciliacao> buscarPorId(String id) {
        return jpaRepository.findById(id).map(RelatorioConciliacaoMapper::paraDominio);
    }
}

package infrastructure.persistence.conciliacao.adapter;

import domain.conciliacao.entity.VinculoConciliacao;
import domain.conciliacao.repository.VinculoConciliacaoRepository;
import infrastructure.persistence.conciliacao.mapper.VinculoConciliacaoMapper;
import infrastructure.persistence.conciliacao.repository.VinculoConciliacaoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class VinculoConciliacaoRepositoryJpaAdapter implements VinculoConciliacaoRepository {

    private final VinculoConciliacaoJpaRepository jpaRepository;

    public VinculoConciliacaoRepositoryJpaAdapter(VinculoConciliacaoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public VinculoConciliacao salvar(VinculoConciliacao vinculo) {
        return VinculoConciliacaoMapper.paraDominio(
                jpaRepository.save(VinculoConciliacaoMapper.paraJpa(vinculo)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VinculoConciliacao> buscarPorDespesaId(String despesaId) {
        return jpaRepository.findByDespesaId(despesaId).map(VinculoConciliacaoMapper::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VinculoConciliacao> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(VinculoConciliacaoMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VinculoConciliacao> listarPorContratoId(String contratoId) {
        return jpaRepository.findByContratoId(contratoId).stream()
                .map(VinculoConciliacaoMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void removerPorDespesaId(String despesaId) {
        jpaRepository.deleteByDespesaId(despesaId);
    }
}

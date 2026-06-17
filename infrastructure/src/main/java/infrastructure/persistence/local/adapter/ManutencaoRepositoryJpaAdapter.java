package infrastructure.persistence.local.adapter;

import domain.local.entity.ManutencaoLocal;
import domain.local.repository.ManutencaoRepository;
import infrastructure.persistence.local.mapper.ManutencaoLocalMapper;
import infrastructure.persistence.local.repository.ManutencaoLocalJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class ManutencaoRepositoryJpaAdapter implements ManutencaoRepository {

    private final ManutencaoLocalJpaRepository jpaRepository;

    public ManutencaoRepositoryJpaAdapter(ManutencaoLocalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ManutencaoLocal salvar(ManutencaoLocal manutencao) {
        return ManutencaoLocalMapper.paraDominio(jpaRepository.save(ManutencaoLocalMapper.paraJpa(manutencao)));
    }

    @Override
    public Optional<ManutencaoLocal> buscarPorId(String id) {
        return jpaRepository.findById(id).map(ManutencaoLocalMapper::paraDominio);
    }

    @Override
    public List<ManutencaoLocal> buscarPorLocalId(String localId) {
        return jpaRepository.findByLocalId(localId).stream()
                .map(ManutencaoLocalMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }
}

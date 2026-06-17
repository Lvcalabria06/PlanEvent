package infrastructure.persistence.local.adapter;

import domain.local.entity.AvaliacaoContextualLocal;
import domain.local.repository.AvaliacaoContextualLocalRepository;
import infrastructure.persistence.local.mapper.AvaliacaoContextualLocalMapper;
import infrastructure.persistence.local.repository.AvaliacaoContextualLocalJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class AvaliacaoContextualLocalRepositoryJpaAdapter implements AvaliacaoContextualLocalRepository {

    private final AvaliacaoContextualLocalJpaRepository jpaRepository;

    public AvaliacaoContextualLocalRepositoryJpaAdapter(AvaliacaoContextualLocalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AvaliacaoContextualLocal salvar(AvaliacaoContextualLocal avaliacao) {
        return AvaliacaoContextualLocalMapper.paraDominio(
                jpaRepository.save(AvaliacaoContextualLocalMapper.paraJpa(avaliacao)));
    }

    @Override
    public Optional<AvaliacaoContextualLocal> buscarPorId(String id) {
        return jpaRepository.findById(id).map(AvaliacaoContextualLocalMapper::paraDominio);
    }

    @Override
    public List<AvaliacaoContextualLocal> buscarPorLocalId(String localId) {
        return jpaRepository.findByLocalId(localId).stream()
                .map(AvaliacaoContextualLocalMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorEventoIdELocalId(String eventoId, String localId) {
        return jpaRepository.existsByEventoIdAndLocalId(eventoId, localId);
    }
}

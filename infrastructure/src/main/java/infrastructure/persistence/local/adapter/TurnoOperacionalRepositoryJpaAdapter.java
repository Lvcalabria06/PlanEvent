package infrastructure.persistence.local.adapter;

import domain.local.turno.entity.TurnoOperacional;
import domain.local.turno.repository.TurnoOperacionalRepository;
import infrastructure.persistence.local.mapper.TurnoOperacionalMapper;
import infrastructure.persistence.local.repository.TurnoOperacionalJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class TurnoOperacionalRepositoryJpaAdapter implements TurnoOperacionalRepository {

    private final TurnoOperacionalJpaRepository jpaRepository;

    public TurnoOperacionalRepositoryJpaAdapter(TurnoOperacionalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TurnoOperacional salvar(TurnoOperacional turno) {
        return TurnoOperacionalMapper.paraDominio(jpaRepository.save(TurnoOperacionalMapper.paraJpa(turno)));
    }

    @Override
    public Optional<TurnoOperacional> buscarPorId(String id) {
        return jpaRepository.findById(id).map(TurnoOperacionalMapper::paraDominio);
    }

    @Override
    public List<TurnoOperacional> buscarPorLocalId(String localId) {
        return jpaRepository.findByLocalId(localId).stream()
                .map(TurnoOperacionalMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }
}

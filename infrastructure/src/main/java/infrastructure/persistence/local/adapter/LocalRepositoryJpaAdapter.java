package infrastructure.persistence.local.adapter;

import domain.local.entity.Local;
import domain.local.repository.LocalRepository;
import infrastructure.persistence.local.mapper.LocalMapper;
import infrastructure.persistence.local.repository.LocalJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class LocalRepositoryJpaAdapter implements LocalRepository {

    private final LocalJpaRepository jpaRepository;

    public LocalRepositoryJpaAdapter(LocalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Local salvar(Local local) {
        return LocalMapper.paraDominio(jpaRepository.save(LocalMapper.paraJpa(local)));
    }

    @Override
    public Optional<Local> buscarPorId(String id) {
        return jpaRepository.findById(id).map(LocalMapper::paraDominio);
    }

    @Override
    public List<Local> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(LocalMapper::paraDominio)
                .toList();
    }
}

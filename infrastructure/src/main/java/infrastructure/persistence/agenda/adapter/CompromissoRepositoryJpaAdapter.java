package infrastructure.persistence.agenda.adapter;

import domain.agenda.entity.Compromisso;
import domain.agenda.repository.CompromissoRepository;
import infrastructure.persistence.agenda.mapper.CompromissoMapper;
import infrastructure.persistence.agenda.repository.CompromissoJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CompromissoRepositoryJpaAdapter implements CompromissoRepository {

    private final CompromissoJpaRepository jpaRepository;

    public CompromissoRepositoryJpaAdapter(CompromissoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Compromisso salvar(Compromisso compromisso) {
        return CompromissoMapper.paraDominio(jpaRepository.save(CompromissoMapper.paraJpa(compromisso)));
    }

    @Override
    public Optional<Compromisso> buscarPorId(String id) {
        return jpaRepository.findById(id).map(CompromissoMapper::paraDominio);
    }

    @Override
    public List<Compromisso> listarPorGestorId(String gestorId) {
        return jpaRepository.findByGestorId(gestorId).stream()
                .map(CompromissoMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Compromisso> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(CompromissoMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }
}

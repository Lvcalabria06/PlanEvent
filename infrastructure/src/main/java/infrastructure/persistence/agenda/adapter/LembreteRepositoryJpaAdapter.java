package infrastructure.persistence.agenda.adapter;

import domain.agenda.entity.Lembrete;
import domain.agenda.repository.LembreteRepository;
import infrastructure.persistence.agenda.mapper.LembreteMapper;
import infrastructure.persistence.agenda.repository.LembreteJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class LembreteRepositoryJpaAdapter implements LembreteRepository {

    private final LembreteJpaRepository jpaRepository;

    public LembreteRepositoryJpaAdapter(LembreteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lembrete salvar(Lembrete lembrete) {
        return LembreteMapper.paraDominio(jpaRepository.save(LembreteMapper.paraJpa(lembrete)));
    }

    @Override
    public Optional<Lembrete> buscarPorId(String id) {
        return jpaRepository.findById(id).map(LembreteMapper::paraDominio);
    }

    @Override
    public List<Lembrete> listarPorCompromissoId(String compromissoId) {
        return jpaRepository.findByCompromissoId(compromissoId).stream()
                .map(LembreteMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Lembrete> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(LembreteMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Lembrete> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(LembreteMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Lembrete> listarPendentesComHorarioAte(LocalDateTime ate) {
        return jpaRepository.findByNotificadoFalseAndHorarioLessThanEqual(ate).stream()
                .map(LembreteMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removerPorCompromissoId(String compromissoId) {
        jpaRepository.deleteByCompromissoId(compromissoId);
    }
}

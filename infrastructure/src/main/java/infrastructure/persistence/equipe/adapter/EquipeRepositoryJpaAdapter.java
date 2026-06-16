package infrastructure.persistence.equipe.adapter;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;
import infrastructure.persistence.equipe.entity.EquipeJpaEntity;
import infrastructure.persistence.equipe.mapper.EquipeMapper;
import infrastructure.persistence.equipe.repository.EquipeJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EquipeRepositoryJpaAdapter implements EquipeRepository {

    private final EquipeJpaRepository jpaRepository;

    public EquipeRepositoryJpaAdapter(EquipeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Equipe salvar(Equipe equipe) {
        EquipeJpaEntity entity = EquipeMapper.paraJpa(equipe);
        EquipeJpaEntity savedEntity = jpaRepository.save(entity);
        return EquipeMapper.paraDominio(savedEntity);
    }

    @Override
    public Optional<Equipe> buscarPorId(String id) {
        return jpaRepository.findById(id).map(EquipeMapper::paraDominio);
    }

    @Override
    public List<Equipe> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(EquipeMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Equipe> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(EquipeMapper::paraDominio)
                .toList();
    }

    @Override
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existeEquipeComNomeNoEvento(String eventoId, String nome) {
        return jpaRepository.existsByEventoIdAndNome(eventoId, nome);
    }

    @Override
    public boolean funcionarioJaEstaEmEquipeNoEvento(String funcionarioId, String eventoId) {
        return jpaRepository.funcionarioJaEstaEmEquipeNoEvento(funcionarioId, eventoId);
    }

    @Override
    public boolean existeFuncionarioVinculado(String funcionarioId) {
        return jpaRepository.existeFuncionarioVinculado(funcionarioId);
    }
}

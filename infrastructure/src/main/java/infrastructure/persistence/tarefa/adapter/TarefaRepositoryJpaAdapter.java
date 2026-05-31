package infrastructure.persistence.tarefa.adapter;

import domain.tarefa.entity.Tarefa;
import domain.tarefa.repository.TarefaRepository;
import infrastructure.persistence.tarefa.mapper.TarefaMapper;
import infrastructure.persistence.tarefa.repository.TarefaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa a porta de domínio {@link TarefaRepository} delegando
 * para o {@link TarefaJpaRepository} (Spring Data) e convertendo entre domínio e
 * mapeamento relacional.
 */
@Repository
public class TarefaRepositoryJpaAdapter implements TarefaRepository {

    private final TarefaJpaRepository jpaRepository;

    public TarefaRepositoryJpaAdapter(TarefaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tarefa salvar(Tarefa tarefa) {
        return TarefaMapper.paraDominio(jpaRepository.save(TarefaMapper.paraJpa(tarefa)));
    }

    @Override
    public Optional<Tarefa> buscarPorId(String id) {
        return jpaRepository.findById(id).map(TarefaMapper::paraDominio);
    }

    @Override
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Tarefa> listarPorEquipeId(String equipeId) {
        return jpaRepository.findByEquipeId(equipeId).stream()
                .map(TarefaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Tarefa> listarPorEventoId(String eventoId) {
        return jpaRepository.findByEventoId(eventoId).stream()
                .map(TarefaMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorTituloEEquipe(String titulo, String equipeId) {
        return jpaRepository.existsByTituloAndEquipeId(titulo, equipeId);
    }

    @Override
    public List<Tarefa> listarPorIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByIdIn(ids).stream()
                .map(TarefaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Tarefa> listarDependentes(String tarefaId) {
        return jpaRepository.findDependentes(tarefaId).stream()
                .map(TarefaMapper::paraDominio)
                .toList();
    }
}

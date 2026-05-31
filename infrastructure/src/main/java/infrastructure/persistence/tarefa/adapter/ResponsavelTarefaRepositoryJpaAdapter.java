package infrastructure.persistence.tarefa.adapter;

import domain.tarefa.entity.ResponsavelTarefa;
import domain.tarefa.repository.ResponsavelTarefaRepository;
import infrastructure.persistence.tarefa.mapper.ResponsavelTarefaMapper;
import infrastructure.persistence.tarefa.repository.ResponsavelTarefaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Adapter que implementa a porta de domínio {@link ResponsavelTarefaRepository}.
 */
@Repository
public class ResponsavelTarefaRepositoryJpaAdapter implements ResponsavelTarefaRepository {

    private final ResponsavelTarefaJpaRepository jpaRepository;

    public ResponsavelTarefaRepositoryJpaAdapter(ResponsavelTarefaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void salvar(ResponsavelTarefa responsavel) {
        jpaRepository.save(ResponsavelTarefaMapper.paraJpa(responsavel));
    }

    @Override
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<ResponsavelTarefa> listarPorTarefa(String tarefaId) {
        return jpaRepository.findByTarefaId(tarefaId).stream()
                .map(ResponsavelTarefaMapper::paraDominio)
                .toList();
    }

    @Override
    public boolean existePorTarefaEFuncionario(String tarefaId, String funcionarioId) {
        return jpaRepository.existsByTarefaIdAndFuncionarioId(tarefaId, funcionarioId);
    }
}

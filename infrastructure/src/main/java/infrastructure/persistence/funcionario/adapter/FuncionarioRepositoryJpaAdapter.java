package infrastructure.persistence.funcionario.adapter;

import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;
import infrastructure.persistence.funcionario.mapper.FuncionarioMapper;
import infrastructure.persistence.funcionario.repository.FuncionarioJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter que implementa a porta de domínio {@link FuncionarioRepository}
 * delegando para o {@link FuncionarioJpaRepository} (Spring Data).
 */
@Repository
public class FuncionarioRepositoryJpaAdapter implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;

    public FuncionarioRepositoryJpaAdapter(FuncionarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        return FuncionarioMapper.paraDominio(jpaRepository.save(FuncionarioMapper.paraJpa(funcionario)));
    }

    @Override
    public Optional<Funcionario> buscarPorId(String id) {
        return jpaRepository.findById(id).map(FuncionarioMapper::paraDominio);
    }

    @Override
    public List<Funcionario> listarTodos() {
        return jpaRepository.findByAtivoTrue().stream()
                .map(FuncionarioMapper::paraDominio)
                .toList();
    }
}

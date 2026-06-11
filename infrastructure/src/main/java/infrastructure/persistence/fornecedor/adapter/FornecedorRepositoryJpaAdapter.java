package infrastructure.persistence.fornecedor.adapter;

import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import infrastructure.persistence.fornecedor.mapper.FornecedorMapper;
import infrastructure.persistence.fornecedor.repository.FornecedorJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class FornecedorRepositoryJpaAdapter implements FornecedorRepository {

    private final FornecedorJpaRepository jpaRepository;

    public FornecedorRepositoryJpaAdapter(FornecedorJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        return FornecedorMapper.paraDominio(jpaRepository.save(FornecedorMapper.paraJpa(fornecedor)));
    }

    @Override
    public Optional<Fornecedor> buscarPorId(String id) {
        return jpaRepository.findById(id).map(FornecedorMapper::paraDominio);
    }

    @Override
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return jpaRepository.findByCnpj(cnpj).map(FornecedorMapper::paraDominio);
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(FornecedorMapper::paraDominio)
                .toList();
    }

    @Override
    @Transactional
    public void remover(String id) {
        jpaRepository.deleteById(id);
    }
}

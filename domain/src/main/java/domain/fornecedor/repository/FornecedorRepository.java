package domain.fornecedor.repository;

import domain.fornecedor.entity.Fornecedor;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository {

    Fornecedor salvar(Fornecedor fornecedor);

    Optional<Fornecedor> buscarPorId(String id);

    Optional<Fornecedor> buscarPorCnpj(String cnpj);

    List<Fornecedor> listarTodos();

    void remover(String id);
}

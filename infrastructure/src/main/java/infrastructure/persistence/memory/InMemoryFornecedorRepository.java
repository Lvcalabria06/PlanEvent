package infrastructure.persistence.memory;

import domain.fornecedor.entity.Fornecedor;
import domain.fornecedor.repository.FornecedorRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryFornecedorRepository implements FornecedorRepository {

    private final Map<String, Fornecedor> porId = new ConcurrentHashMap<>();
    private final Map<String, String> cnpjParaId = new ConcurrentHashMap<>();

    @Override
    public Fornecedor salvar(Fornecedor fornecedor) {
        porId.put(fornecedor.getId(), fornecedor);
        cnpjParaId.put(fornecedor.getCnpj(), fornecedor.getId());
        return fornecedor;
    }

    @Override
    public Optional<Fornecedor> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        String id = cnpjParaId.get(cnpj);
        return id == null ? Optional.empty() : buscarPorId(id);
    }

    @Override
    public List<Fornecedor> listarTodos() {
        return List.copyOf(porId.values());
    }

    @Override
    public void remover(String id) {
        Fornecedor fornecedor = porId.remove(id);
        if (fornecedor != null) {
            cnpjParaId.remove(fornecedor.getCnpj());
        }
    }

    public void limpar() {
        porId.clear();
        cnpjParaId.clear();
    }
}

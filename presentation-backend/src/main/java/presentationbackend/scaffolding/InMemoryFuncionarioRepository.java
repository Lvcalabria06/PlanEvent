package presentationbackend.scaffolding;

import domain.funcionario.entity.Funcionario;
import domain.funcionario.repository.FuncionarioRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub provisório de persistência de Funcionário (ver
 * {@link InMemoryEventoRepository}).
 */
public class InMemoryFuncionarioRepository implements FuncionarioRepository {

    private final Map<String, Funcionario> dados = new ConcurrentHashMap<>();

    @Override
    public Funcionario salvar(Funcionario funcionario) {
        dados.put(funcionario.getId(), funcionario);
        return funcionario;
    }

    @Override
    public Optional<Funcionario> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Funcionario> listarTodos() {
        return List.copyOf(dados.values());
    }
}

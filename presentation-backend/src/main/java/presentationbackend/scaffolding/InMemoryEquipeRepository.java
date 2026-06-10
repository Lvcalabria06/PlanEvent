package presentationbackend.scaffolding;

import domain.equipe.entity.Equipe;
import domain.equipe.repository.EquipeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub provisório de persistência de Equipe (ver
 * {@link InMemoryEventoRepository}).
 */
public class InMemoryEquipeRepository implements EquipeRepository {

    private final Map<String, Equipe> dados = new ConcurrentHashMap<>();

    @Override
    public Equipe salvar(Equipe equipe) {
        dados.put(equipe.getId(), equipe);
        return equipe;
    }

    @Override
    public Optional<Equipe> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Equipe> listarPorEventoId(String eventoId) {
        return dados.values().stream()
                .filter(e -> e.getEventoId().equals(eventoId))
                .toList();
    }

    @Override
    public List<Equipe> listarTodos() {
        return List.copyOf(dados.values());
    }

    @Override
    public void remover(String id) {
        dados.remove(id);
    }

    @Override
    public boolean existeEquipeComNomeNoEvento(String eventoId, String nome) {
        return dados.values().stream()
                .anyMatch(e -> e.getEventoId().equals(eventoId) && e.getNome().equalsIgnoreCase(nome));
    }

    @Override
    public boolean funcionarioJaEstaEmEquipeNoEvento(String funcionarioId, String eventoId) {
        return dados.values().stream()
                .filter(e -> e.getEventoId().equals(eventoId))
                .anyMatch(e -> e.possuiMembro(funcionarioId));
    }

    @Override
    public boolean existeFuncionarioVinculado(String funcionarioId) {
        return dados.values().stream()
                .anyMatch(e -> e.possuiMembro(funcionarioId));
    }
}

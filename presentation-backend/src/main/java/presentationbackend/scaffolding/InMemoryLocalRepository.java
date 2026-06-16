package presentationbackend.scaffolding;

import domain.local.entity.Local;
import domain.local.repository.LocalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLocalRepository implements LocalRepository {

    private final Map<String, Local> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized Local salvar(Local local) {
        porId.put(local.getId(), local);
        return local;
    }

    @Override
    public Optional<Local> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<Local> listarTodos() {
        return new ArrayList<>(porId.values());
    }
}

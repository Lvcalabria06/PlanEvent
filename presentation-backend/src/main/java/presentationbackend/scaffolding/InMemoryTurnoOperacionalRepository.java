package presentationbackend.scaffolding;

import domain.local.turno.entity.TurnoOperacional;
import domain.local.turno.repository.TurnoOperacionalRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryTurnoOperacionalRepository implements TurnoOperacionalRepository {

    private final Map<String, TurnoOperacional> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized TurnoOperacional salvar(TurnoOperacional turno) {
        porId.put(turno.getId(), turno);
        return turno;
    }

    @Override
    public Optional<TurnoOperacional> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<TurnoOperacional> buscarPorLocalId(String localId) {
        return porId.values().stream()
                .filter(t -> localId.equals(t.getLocalId()))
                .collect(Collectors.toList());
    }

    @Override
    public void remover(String id) {
        porId.remove(id);
    }
}

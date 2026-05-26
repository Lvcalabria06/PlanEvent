package domain.estoque.support;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.repository.ConsumoEventoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryConsumoEventoRepository implements ConsumoEventoRepository {

    private final Map<String, ConsumoEvento> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized ConsumoEvento salvar(ConsumoEvento consumoEvento) {
        porId.put(consumoEvento.getId(), consumoEvento);
        return consumoEvento;
    }

    @Override
    public Optional<ConsumoEvento> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<ConsumoEvento> listarTodos() {
        return new ArrayList<>(porId.values());
    }

    @Override
    public List<ConsumoEvento> listarPorEvento(String eventoId) {
        return porId.values().stream()
                .filter(c -> c.getEventoId().equals(eventoId))
                .collect(Collectors.toList());
    }

    public void limpar() {
        porId.clear();
    }
}

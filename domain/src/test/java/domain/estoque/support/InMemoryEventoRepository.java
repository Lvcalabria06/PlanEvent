package domain.estoque.support;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventoRepository implements EventoRepository {

    private final Map<String, Evento> porId = new ConcurrentHashMap<>();

    @Override
    public Evento salvar(Evento evento) {
        porId.put(evento.getId(), evento);
        return evento;
    }

    @Override
    public Optional<Evento> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<Evento> listarTodos() {
        return new ArrayList<>(porId.values());
    }

    public void limpar() {
        porId.clear();
    }
}

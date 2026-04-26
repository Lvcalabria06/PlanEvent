package domain.local.support;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventoRepository implements EventoRepository {

    private final Map<String, Evento> map = new ConcurrentHashMap<>();

    @Override
    public Evento salvar(Evento evento) {
        map.put(evento.getId(), evento);
        return evento;
    }

    @Override
    public Optional<Evento> buscarPorId(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public void limpar() {
        map.clear();
    }
}

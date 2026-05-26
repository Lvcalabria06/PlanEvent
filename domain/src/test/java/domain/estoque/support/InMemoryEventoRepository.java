package domain.estoque.support;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

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

    public void limpar() {
        porId.clear();
    }
}

package infrastructure.persistence.memory;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
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
    public java.util.List<Evento> listarTodos() {
        return new java.util.ArrayList<>(porId.values());
    }

    public void limpar() {
        porId.clear();
    }
}

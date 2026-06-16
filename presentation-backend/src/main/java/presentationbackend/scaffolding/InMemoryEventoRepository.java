package presentationbackend.scaffolding;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub provisório de persistência de Evento, apenas para viabilizar o teste
 * fim-a-fim da fatia de Tarefas/Dependências. Substituído automaticamente quando
 * o módulo de Eventos fornecer seu próprio bean (ver {@code @ConditionalOnMissingBean}).
 */
public class InMemoryEventoRepository implements EventoRepository {

    private final Map<String, Evento> dados = new ConcurrentHashMap<>();

    @Override
    public Evento salvar(Evento evento) {
        dados.put(evento.getId(), evento);
        return evento;
    }

    @Override
    public Optional<Evento> buscarPorId(String id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Evento> listarTodos() {
        return new ArrayList<>(dados.values());
    }
}

package domain.local.support;

import domain.local.entity.AgendaLocal;
import domain.local.repository.AgendaLocalRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryAgendaLocalRepository implements AgendaLocalRepository {

    private final Map<String, AgendaLocal> porId = new ConcurrentHashMap<>();
    private final Map<String, String> localIdParaAgendaId = new ConcurrentHashMap<>();

    @Override
    public synchronized AgendaLocal salvar(AgendaLocal agenda) {
        porId.put(agenda.getId(), agenda);
        localIdParaAgendaId.put(agenda.getLocalId(), agenda.getId());
        return agenda;
    }

    @Override
    public Optional<AgendaLocal> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public Optional<AgendaLocal> buscarPorLocalId(String localId) {
        String id = localIdParaAgendaId.get(localId);
        return id == null ? Optional.empty() : Optional.ofNullable(porId.get(id));
    }

    public void limpar() {
        porId.clear();
        localIdParaAgendaId.clear();
    }
}

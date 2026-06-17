package domain.local.support;

import domain.local.entity.AvaliacaoContextualLocal;
import domain.local.repository.AvaliacaoContextualLocalRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAvaliacaoContextualRepository implements AvaliacaoContextualLocalRepository {

    private final Map<String, AvaliacaoContextualLocal> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized AvaliacaoContextualLocal salvar(AvaliacaoContextualLocal avaliacao) {
        porId.put(avaliacao.getId(), avaliacao);
        return avaliacao;
    }

    @Override
    public Optional<AvaliacaoContextualLocal> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<AvaliacaoContextualLocal> buscarPorLocalId(String localId) {
        return porId.values().stream()
                .filter(a -> localId.equals(a.getLocalId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorEventoIdELocalId(String eventoId, String localId) {
        return porId.values().stream()
                .anyMatch(a -> eventoId.equals(a.getEventoId()) && localId.equals(a.getLocalId()));
    }

    public void limpar() {
        porId.clear();
    }
}

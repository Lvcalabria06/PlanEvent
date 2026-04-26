package domain.local.support;

import domain.local.entity.AvaliacaoLocalEvento;
import domain.local.repository.AvaliacaoLocalEventoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAvaliacaoLocalEventoRepository implements AvaliacaoLocalEventoRepository {

    private final Map<String, AvaliacaoLocalEvento> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized AvaliacaoLocalEvento salvar(AvaliacaoLocalEvento avaliacao) {
        porId.put(avaliacao.getId(), avaliacao);
        return avaliacao;
    }

    @Override
    public Optional<AvaliacaoLocalEvento> buscarPorEventoIdELocalId(String eventoId, String localId) {
        return porId.values().stream()
                .filter(a -> eventoId.equals(a.getEventoId()) && localId.equals(a.getLocalId()))
                .findFirst();
    }

    @Override
    public List<AvaliacaoLocalEvento> listarPorLocalId(String localId) {
        return porId.values().stream()
                .filter(a -> localId.equals(a.getLocalId()))
                .collect(Collectors.toList());
    }

    public void limpar() {
        porId.clear();
    }
}

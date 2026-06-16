package presentationbackend.scaffolding;

import domain.local.entity.ManutencaoLocal;
import domain.local.repository.ManutencaoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryManutencaoRepository implements ManutencaoRepository {

    private final Map<String, ManutencaoLocal> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized ManutencaoLocal salvar(ManutencaoLocal manutencao) {
        porId.put(manutencao.getId(), manutencao);
        return manutencao;
    }

    @Override
    public Optional<ManutencaoLocal> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<ManutencaoLocal> buscarPorLocalId(String localId) {
        return porId.values().stream()
                .filter(m -> localId.equals(m.getLocalId()))
                .collect(Collectors.toList());
    }

    @Override
    public void remover(String id) {
        porId.remove(id);
    }
}

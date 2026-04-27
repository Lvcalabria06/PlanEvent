package domain.local.support;

import domain.local.entity.IndisponibilidadeLocal;
import domain.local.repository.IndisponibilidadeLocalRepository;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryIndisponibilidadeLocalRepository implements IndisponibilidadeLocalRepository {

    private final Map<String, IndisponibilidadeLocal> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized IndisponibilidadeLocal salvar(IndisponibilidadeLocal indisponibilidade) {
        porId.put(indisponibilidade.getId(), indisponibilidade);
        return indisponibilidade;
    }

    @Override
    public Optional<IndisponibilidadeLocal> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<IndisponibilidadeLocal> listarPorLocalId(String localId) {
        return porId.values().stream()
                .filter(i -> localId.equals(i.getLocalId()))
                .collect(Collectors.toList());
    }

    public void limpar() {
        porId.clear();
    }
}

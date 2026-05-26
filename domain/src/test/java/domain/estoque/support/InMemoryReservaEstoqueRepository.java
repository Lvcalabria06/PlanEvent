package domain.estoque.support;

import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.ReservaEstoqueRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryReservaEstoqueRepository implements ReservaEstoqueRepository {

    private final Map<String, ReservaEstoque> porId = new ConcurrentHashMap<>();

    @Override
    public synchronized ReservaEstoque salvar(ReservaEstoque reservaEstoque) {
        porId.put(reservaEstoque.getId(), reservaEstoque);
        return reservaEstoque;
    }

    @Override
    public Optional<ReservaEstoque> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<ReservaEstoque> listarTodas() {
        return new ArrayList<>(porId.values());
    }

    @Override
    public List<ReservaEstoque> listarPorEvento(String eventoId) {
        return porId.values().stream()
                .filter(r -> r.getEventoId().equals(eventoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return porId.values().stream()
                .filter(r -> r.sobrepoePeriodo(inicio, fim))
                .collect(Collectors.toList());
    }

    public void limpar() {
        porId.clear();
    }
}

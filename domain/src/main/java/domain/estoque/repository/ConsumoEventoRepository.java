package domain.estoque.repository;

import domain.estoque.entity.ConsumoEvento;

import java.util.List;
import java.util.Optional;

public interface ConsumoEventoRepository {
    ConsumoEvento salvar(ConsumoEvento consumoEvento);
    Optional<ConsumoEvento> buscarPorId(String id);
    List<ConsumoEvento> listarTodos();
    List<ConsumoEvento> listarPorEvento(String eventoId);
}

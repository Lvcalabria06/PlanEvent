package domain.estoque.service;

import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;

import java.util.List;
import java.util.Optional;

public interface ConsumoEventoService {

    ConsumoEvento registrar(String eventoId, String usuarioId, List<ItemConsumoEvento> itensConsumidos);

    void invalidar(String consumoEventoId);

    Optional<ConsumoEvento> buscarPorId(String id);

    List<ConsumoEvento> listarPorEvento(String eventoId);

    List<ConsumoEvento> listarTodos();
}

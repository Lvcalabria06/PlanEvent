package domain.estoque.repository;

import domain.estoque.entity.ConsumoEvento;

import java.util.List;

public interface ConsumoEventoRepository {
    ConsumoEvento salvar(ConsumoEvento consumoEvento);
    List<ConsumoEvento> listarTodos();
}

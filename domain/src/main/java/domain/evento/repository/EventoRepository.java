package domain.evento.repository;

import domain.evento.entity.Evento;

import java.util.List;
import java.util.Optional;

public interface EventoRepository {
    Evento salvar(Evento evento);

    Optional<Evento> buscarPorId(String id);

    List<Evento> listarTodos();
}

package domain.evento.repository;

import java.util.Optional;

import domain.evento.entity.Evento;

public interface EventoRepository {
    Optional<Evento> buscarPorId(String id);
}

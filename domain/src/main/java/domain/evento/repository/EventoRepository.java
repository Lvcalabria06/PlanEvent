package domain.evento.repository;

import java.util.Optional;

import domain.evento.entity.Evento;

public interface EventoRepository {
    Evento salvar(Evento evento);
    Optional<Evento> buscarPorId(String id);
}

package domain.agenda.repository;

import domain.agenda.entity.Lembrete;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LembreteRepository {

    Lembrete salvar(Lembrete lembrete);

    Optional<Lembrete> buscarPorId(String id);

    List<Lembrete> listarPorCompromissoId(String compromissoId);

    List<Lembrete> listarPorEventoId(String eventoId);

    List<Lembrete> listarTodos();

    List<Lembrete> listarPendentesComHorarioAte(LocalDateTime ate);

    void remover(String id);

    void removerPorCompromissoId(String compromissoId);
}

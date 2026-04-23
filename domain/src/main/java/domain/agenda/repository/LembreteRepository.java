package domain.agenda.repository;

import domain.agenda.entity.Lembrete;

import java.util.List;
import java.util.Optional;

public interface LembreteRepository {

    Lembrete salvar(Lembrete lembrete);

    Optional<Lembrete> buscarPorId(String id);

    List<Lembrete> listarPorCompromissoId(String compromissoId);

    void remover(String id);

    void removerPorCompromissoId(String compromissoId);
}

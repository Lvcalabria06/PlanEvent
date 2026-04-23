package domain.agenda.repository;

import domain.agenda.entity.Compromisso;

import java.util.List;
import java.util.Optional;

public interface CompromissoRepository {

    Compromisso salvar(Compromisso compromisso);

    Optional<Compromisso> buscarPorId(String id);

    List<Compromisso> listarPorGestorId(String gestorId);

    void remover(String id);
}

package domain.agenda.service;

import domain.agenda.entity.Compromisso;

import java.util.List;

public interface CompromissoService {

    Compromisso criarCompromisso(Compromisso compromisso);

    Compromisso editarCompromisso(Compromisso compromisso);

    Compromisso buscarCompromisso(String id);

    List<Compromisso> listarCompromissosPorGestor(String gestorId);

    List<Compromisso> listarTodosCompromissos();

    void removerCompromisso(String id);

    Compromisso iniciarCompromisso(String id);

    Compromisso concluirCompromisso(String id);

    Compromisso cancelarCompromisso(String id);
}

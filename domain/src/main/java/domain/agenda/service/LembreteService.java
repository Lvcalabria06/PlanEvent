package domain.agenda.service;

import domain.agenda.entity.Lembrete;

import java.util.List;

public interface LembreteService {

    Lembrete criarLembrete(Lembrete lembrete);

    Lembrete editarLembrete(Lembrete lembrete);

    Lembrete buscarLembrete(String id);

    List<Lembrete> listarLembretesPorCompromisso(String compromissoId);

    List<Lembrete> listarLembretesPorEvento(String eventoId);

    List<Lembrete> listarLembretesPorGestor(String gestorId);

    List<Lembrete> listarTodosLembretes();

    void removerLembrete(String id);

    Lembrete dispararNotificacao(String lembreteId);

    List<Lembrete> processarLembretesVencidos();
}

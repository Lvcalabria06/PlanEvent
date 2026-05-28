package domain.agenda.service;

import domain.agenda.entity.Lembrete;

import java.util.List;

public interface LembreteService {

    Lembrete criarLembrete(Lembrete lembrete);

    Lembrete editarLembrete(Lembrete lembrete);

    Lembrete buscarLembrete(String id);

    List<Lembrete> listarLembretesPorCompromisso(String compromissoId);

    void removerLembrete(String id);

    /**
     * Dispara a notificacao de um lembrete pendente (RN6 / CA6), notificando
     * todos os observadores registrados (envio de alerta, marcacao, etc.).
     */
    Lembrete dispararNotificacao(String lembreteId);
}

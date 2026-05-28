package domain.agenda.observer;

import domain.agenda.entity.Lembrete;

/**
 * Observer do fluxo de notificacao de lembretes (RN6 / CA6).
 * Permite reagir ao disparo de um alerta sem acoplar o servico a canais
 * especificos (e-mail, push, persistencia, auditoria).
 */
public interface LembreteObserver {

    void onLembreteDisparado(Lembrete lembrete);
}

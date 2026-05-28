package domain.agenda.observer;

import domain.agenda.entity.Lembrete;

public interface LembreteObserver {

    void onLembreteDisparado(Lembrete lembrete);
}

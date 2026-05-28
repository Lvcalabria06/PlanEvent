package domain.agenda.observer;

import domain.agenda.entity.Lembrete;

public class EnviarAlertaLembreteObserver implements LembreteObserver {

    @Override
    public void onLembreteDisparado(Lembrete lembrete) {
        if (lembrete.isNotificado()) {
            return;
        }
    }
}

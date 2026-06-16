package domain.agenda.observer;

import domain.agenda.entity.Lembrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LembreteNotificacaoSubject {

    private final List<LembreteObserver> observadores = new ArrayList<>();

    public void registrar(LembreteObserver observador) {
        if (observador == null) {
            throw new IllegalArgumentException("Observador nao pode ser nulo.");
        }
        observadores.add(observador);
    }

    public List<LembreteObserver> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    public void notificar(Lembrete lembrete) {
        if (lembrete == null) {
            throw new IllegalArgumentException("Lembrete nao pode ser nulo.");
        }
        for (LembreteObserver observador : observadores) {
            observador.onLembreteDisparado(lembrete);
        }
    }
}

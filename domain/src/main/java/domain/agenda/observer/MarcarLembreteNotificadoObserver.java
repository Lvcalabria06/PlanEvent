package domain.agenda.observer;

import domain.agenda.entity.Lembrete;
import domain.agenda.repository.LembreteRepository;

public class MarcarLembreteNotificadoObserver implements LembreteObserver {

    private final LembreteRepository lembreteRepository;

    public MarcarLembreteNotificadoObserver(LembreteRepository lembreteRepository) {
        this.lembreteRepository = lembreteRepository;
    }

    @Override
    public void onLembreteDisparado(Lembrete lembrete) {
        lembrete.marcarComoNotificado();
        lembreteRepository.salvar(lembrete);
    }
}

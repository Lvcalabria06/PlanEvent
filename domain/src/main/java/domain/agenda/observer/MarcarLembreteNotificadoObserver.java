package domain.agenda.observer;

import domain.agenda.entity.Lembrete;
import domain.agenda.repository.LembreteRepository;

/**
 * Observer que aplica RN6: apos o alerta, marca o lembrete como notificado
 * e persiste o estado.
 */
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

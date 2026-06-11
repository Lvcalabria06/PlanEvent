package domain.agenda.observer;

import domain.agenda.entity.Lembrete;
import domain.agenda.port.AlertaLembretePort;

public class EnviarAlertaLembreteObserver implements LembreteObserver {

    private final AlertaLembretePort alertaPort;

    public EnviarAlertaLembreteObserver(AlertaLembretePort alertaPort) {
        if (alertaPort == null) {
            throw new IllegalArgumentException("Porta de alerta é obrigatória.");
        }
        this.alertaPort = alertaPort;
    }

    @Override
    public void onLembreteDisparado(Lembrete lembrete) {
        if (lembrete.isNotificado()) {
            return;
        }
        alertaPort.enviar(lembrete);
    }
}

package domain.agenda.observer;

import domain.agenda.entity.Lembrete;

/**
 * Observer responsavel pelo envio do alerta ao gestor. Implementacao de dominio
 * minimalista; canais reais (e-mail, push) podem ser adicionados como novos
 * observadores ou adaptadores na camada de infraestrutura.
 */
public class EnviarAlertaLembreteObserver implements LembreteObserver {

    @Override
    public void onLembreteDisparado(Lembrete lembrete) {
        if (lembrete.isNotificado()) {
            return;
        }
        // Ponto de extensao: integracao com servicos de notificacao externos.
    }
}

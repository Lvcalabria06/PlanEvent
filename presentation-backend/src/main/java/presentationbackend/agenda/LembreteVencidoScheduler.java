package presentationbackend.agenda;

import application.agenda.usecase.LembreteUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Dispara automaticamente lembretes cujo horário já passou (RN6).
 */
@Component
public class LembreteVencidoScheduler {

    private static final Logger log = LoggerFactory.getLogger(LembreteVencidoScheduler.class);

    private final LembreteUseCase lembreteUseCase;

    public LembreteVencidoScheduler(LembreteUseCase lembreteUseCase) {
        this.lembreteUseCase = lembreteUseCase;
    }

    @Scheduled(fixedRate = 60_000)
    public void processarLembretesVencidos() {
        var alertas = lembreteUseCase.processarVencidos();
        if (!alertas.isEmpty()) {
            log.debug("Scheduler processou {} lembrete(s) vencido(s).", alertas.size());
        }
    }
}

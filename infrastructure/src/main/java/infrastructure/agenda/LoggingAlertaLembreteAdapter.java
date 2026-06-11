package infrastructure.agenda;

import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.port.AlertaLembreteMensagem;
import domain.agenda.port.AlertaLembretePort;
import domain.agenda.repository.CompromissoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adapter que entrega alertas de lembrete via log (demonstração).
 * Em produção, substituir por e-mail, push ou outro canal.
 */
@Component
public class LoggingAlertaLembreteAdapter implements AlertaLembretePort {

    private static final Logger log = LoggerFactory.getLogger(LoggingAlertaLembreteAdapter.class);

    private final CompromissoRepository compromissoRepository;

    public LoggingAlertaLembreteAdapter(CompromissoRepository compromissoRepository) {
        this.compromissoRepository = compromissoRepository;
    }

    @Override
    public void enviar(Lembrete lembrete) {
        String titulo = null;
        if (lembrete.getCompromissoId() != null) {
            titulo = compromissoRepository.buscarPorId(lembrete.getCompromissoId())
                    .map(Compromisso::getTitulo)
                    .orElse(null);
        }
        String mensagem = AlertaLembreteMensagem.paraLembrete(lembrete, titulo);
        log.info("[ALERTA LEMBRETE] id={} — {}", lembrete.getId(), mensagem);
    }
}

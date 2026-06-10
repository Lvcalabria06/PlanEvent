package domain.agenda.port;

import domain.agenda.entity.Lembrete;

/**
 * Porta de saída para entrega de alertas de lembrete ao gestor (e-mail, push, log, etc.).
 */
public interface AlertaLembretePort {

    void enviar(Lembrete lembrete);
}

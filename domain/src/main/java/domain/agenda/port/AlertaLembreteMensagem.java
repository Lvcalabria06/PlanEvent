package domain.agenda.port;

import domain.agenda.entity.Lembrete;

/**
 * Monta a mensagem exibida ao gestor quando um lembrete é disparado.
 */
public final class AlertaLembreteMensagem {

    private AlertaLembreteMensagem() {
    }

    public static String paraLembrete(Lembrete lembrete, String tituloCompromisso) {
        if (tituloCompromisso != null && !tituloCompromisso.isBlank()) {
            return "Lembrete: \"" + tituloCompromisso.trim() + "\" — alerta às "
                    + formatarHorario(lembrete);
        }
        if (lembrete.getEventoId() != null) {
            return "Lembrete do evento — alerta às " + formatarHorario(lembrete);
        }
        return "Lembrete agendado para " + formatarHorario(lembrete);
    }

    private static String formatarHorario(Lembrete lembrete) {
        return lembrete.getHorario().toLocalDate() + " às "
                + lembrete.getHorario().toLocalTime().withSecond(0).withNano(0);
    }
}

package domain.agenda.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Lembrete {
    private final String id;
    private final String compromissoId;
    private final String eventoId;
    private LocalDateTime horario;
    private boolean notificado;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lembrete(String compromissoId, String eventoId, LocalDateTime horario, LocalDateTime inicioReferencia) {
        boolean hasCompromisso = compromissoId != null && !compromissoId.trim().isEmpty();
        boolean hasEvento = eventoId != null && !eventoId.trim().isEmpty();
        if (!hasCompromisso && !hasEvento) {
            throw new IllegalArgumentException("O lembrete deve estar vinculado a um compromisso ou a um evento.");
        }
        if (horario == null) {
            throw new IllegalArgumentException("O horário do lembrete é obrigatório.");
        }
        if (horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é permitido criar lembretes com horário no passado.");
        }
        if (inicioReferencia != null && !horario.isBefore(inicioReferencia)) {
            throw new IllegalArgumentException("O horário do lembrete deve ser anterior ao início do evento/compromisso.");
        }

        this.id = UUID.randomUUID().toString();
        this.compromissoId = compromissoId;
        this.eventoId = eventoId;
        this.horario = horario;
        this.notificado = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    private Lembrete(String id, String compromissoId, String eventoId, LocalDateTime horario,
                     boolean notificado, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.compromissoId = compromissoId;
        this.eventoId = eventoId;
        this.horario = horario;
        this.notificado = notificado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Reconstrói um Lembrete a partir de dados já persistidos, sem revalidar
     * regras de criação. Usado exclusivamente pela camada de persistência.
     */
    public static Lembrete reconstituir(String id, String compromissoId, String eventoId,
                                        LocalDateTime horario, boolean notificado,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Lembrete(id, compromissoId, eventoId, horario, notificado, createdAt, updatedAt);
    }

    public void editar(LocalDateTime novoHorario, LocalDateTime inicioReferencia) {
        if (this.notificado) {
            throw new IllegalStateException("Não é permitido editar lembretes já notificados.");
        }
        if (novoHorario == null) {
            throw new IllegalArgumentException("O horário do lembrete é obrigatório.");
        }
        if (inicioReferencia != null && !novoHorario.isBefore(inicioReferencia)) {
            throw new IllegalArgumentException("O horário do lembrete deve ser anterior ao início do evento/compromisso.");
        }
        this.horario = novoHorario;
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarComoNotificado() {
        if (this.notificado) {
            throw new IllegalStateException("Lembrete já foi marcado como notificado.");
        }
        this.notificado = true;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean temMesmoHorario(Lembrete outro) {
        boolean mesmoVinculo = false;
        if (this.compromissoId != null && this.compromissoId.equals(outro.compromissoId)) {
            mesmoVinculo = true;
        } else if (this.eventoId != null && this.eventoId.equals(outro.eventoId)) {
            mesmoVinculo = true;
        }

        return mesmoVinculo
                && this.horario.equals(outro.horario)
                && !this.id.equals(outro.id);
    }

    public String getId() { return id; }
    public String getCompromissoId() { return compromissoId; }
    public String getEventoId() { return eventoId; }
    public LocalDateTime getHorario() { return horario; }
    public boolean isNotificado() { return notificado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

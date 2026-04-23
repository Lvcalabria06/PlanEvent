package domain.agenda.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Lembrete {
    private final String id;
    private final String compromissoId;
    private LocalDateTime horario;
    private boolean notificado;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lembrete(String compromissoId, LocalDateTime horario, LocalDateTime inicioCompromisso) {
        if (compromissoId == null || compromissoId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do compromisso é obrigatório.");
        }
        if (horario == null) {
            throw new IllegalArgumentException("O horário do lembrete é obrigatório.");
        }
        if (horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é permitido criar lembretes com horário no passado.");
        }
        if (inicioCompromisso != null && !horario.isBefore(inicioCompromisso)) {
            throw new IllegalArgumentException("O horário do lembrete deve ser anterior ao início do compromisso.");
        }

        this.id = UUID.randomUUID().toString();
        this.compromissoId = compromissoId;
        this.horario = horario;
        this.notificado = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void editar(LocalDateTime novoHorario, LocalDateTime inicioCompromisso) {
        if (this.notificado) {
            throw new IllegalStateException("Não é permitido editar lembretes já notificados.");
        }
        if (novoHorario == null) {
            throw new IllegalArgumentException("O horário do lembrete é obrigatório.");
        }
        if (inicioCompromisso != null && !novoHorario.isBefore(inicioCompromisso)) {
            throw new IllegalArgumentException("O horário do lembrete deve ser anterior ao início do compromisso.");
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
        return this.compromissoId.equals(outro.compromissoId)
                && this.horario.equals(outro.horario)
                && !this.id.equals(outro.id);
    }

    public String getId() { return id; }
    public String getCompromissoId() { return compromissoId; }
    public LocalDateTime getHorario() { return horario; }
    public boolean isNotificado() { return notificado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

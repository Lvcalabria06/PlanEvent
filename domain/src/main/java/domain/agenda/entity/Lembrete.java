package domain.agenda.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Lembrete {
    private final String id;
    private final String compromissoId;
    private LocalDateTime horario;
    private boolean notificado;
    private final LocalDateTime createdAt;

    public Lembrete() {
        this.id = UUID.randomUUID().toString();
        this.compromissoId = null;
        this.createdAt = LocalDateTime.now();
    }

    public Lembrete(String compromissoId, LocalDateTime horario) {
        if (compromissoId == null) {
            throw new IllegalArgumentException("O ID do compromisso é obrigatório.");
        }
        if (horario == null || horario.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário do lembrete deve ser no futuro.");
        }
        this.id = UUID.randomUUID().toString();
        this.compromissoId = compromissoId;
        this.horario = horario;
        this.notificado = false;
        this.createdAt = LocalDateTime.now();
    }

    public void marcarComoNotificado() {
        if (this.notificado) {
            throw new IllegalStateException("Lembrete já foi marcado como notificado.");
        }
        this.notificado = true;
    }

    // Getters
    public String getId() { return id; }
    public String getCompromissoId() { return compromissoId; }
    public LocalDateTime getHorario() { return horario; }
    public boolean isNotificado() { return notificado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

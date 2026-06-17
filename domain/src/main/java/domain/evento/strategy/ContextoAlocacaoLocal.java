package domain.evento.strategy;

import domain.evento.entity.Evento;
import domain.local.entity.Local;

import java.math.BigDecimal;

public final class ContextoAlocacaoLocal {

    private final Evento evento;
    private final Local local;
    private final BigDecimal teto;
    private final BigDecimal custo;
    private final boolean capacidadeOk;
    private final boolean acimaDoTeto;
    private final boolean avaliarAgenda;
    private final boolean agendaOk;
    private final String motivoAgenda;
    private final boolean infraOk;

    public ContextoAlocacaoLocal(
            Evento evento,
            Local local,
            BigDecimal teto,
            BigDecimal custo,
            boolean capacidadeOk,
            boolean acimaDoTeto,
            boolean avaliarAgenda,
            boolean agendaOk,
            String motivoAgenda,
            boolean infraOk) {
        this.evento = evento;
        this.local = local;
        this.teto = teto;
        this.custo = custo;
        this.capacidadeOk = capacidadeOk;
        this.acimaDoTeto = acimaDoTeto;
        this.avaliarAgenda = avaliarAgenda;
        this.agendaOk = agendaOk;
        this.motivoAgenda = motivoAgenda;
        this.infraOk = infraOk;
    }

    public Evento getEvento() {
        return evento;
    }

    public Local getLocal() {
        return local;
    }

    public BigDecimal getTeto() {
        return teto;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public boolean isCapacidadeOk() {
        return capacidadeOk;
    }

    public boolean isAcimaDoTeto() {
        return acimaDoTeto;
    }

    public boolean isAvaliarAgenda() {
        return avaliarAgenda;
    }

    public boolean isAgendaOk() {
        return agendaOk;
    }

    public String getMotivoAgenda() {
        return motivoAgenda;
    }

    public boolean isInfraOk() {
        return infraOk;
    }
}

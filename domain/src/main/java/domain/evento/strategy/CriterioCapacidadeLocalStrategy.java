package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioCapacidadeLocalStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return contexto.getLocal().isAtivo()
                && agendaLiberada(contexto)
                && !contexto.isCapacidadeOk();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        return CriterioLocalInativoStrategy.candidato(
                contexto,
                ClassificacaoAlocacaoLocal.INADEQUADO,
                String.format(
                        "Capacidade %d lugares insuficiente para %d participantes estimados do evento.",
                        contexto.getLocal().getCapacidade(),
                        contexto.getEvento().getQuantidadeEstimadaParticipantes()),
                true);
    }

    private static boolean agendaLiberada(ContextoAlocacaoLocal contexto) {
        return !contexto.isAvaliarAgenda() || contexto.isAgendaOk();
    }
}

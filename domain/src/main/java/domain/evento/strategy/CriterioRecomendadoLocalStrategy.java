package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioRecomendadoLocalStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return contexto.getLocal().isAtivo()
                && agendaLiberada(contexto)
                && contexto.isCapacidadeOk()
                && !contexto.isAcimaDoTeto()
                && contexto.isInfraOk();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        String justificativa = String.format(
                "Capacidade %d lugares ≥ %d participantes; custo R$ %s dentro do teto R$ %s; infraestrutura compatível",
                contexto.getLocal().getCapacidade(),
                contexto.getEvento().getQuantidadeEstimadaParticipantes(),
                contexto.getCusto(),
                contexto.getTeto());
        if (contexto.isAvaliarAgenda()) {
            justificativa += "; disponível na janela do evento.";
        } else {
            justificativa += ".";
        }
        return new CandidatoAnaliseLocal(
                contexto.getLocal().getId(),
                contexto.getLocal().getNome(),
                ClassificacaoAlocacaoLocal.RECOMENDADO,
                justificativa,
                contexto.getCusto(),
                contexto.getLocal().getCapacidade(),
                false,
                contexto.isCapacidadeOk(),
                true);
    }

    private static boolean agendaLiberada(ContextoAlocacaoLocal contexto) {
        return !contexto.isAvaliarAgenda() || contexto.isAgendaOk();
    }
}

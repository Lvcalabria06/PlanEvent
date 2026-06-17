package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioCustoTetoLocalStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return contexto.getLocal().isAtivo()
                && agendaLiberada(contexto)
                && contexto.isCapacidadeOk()
                && contexto.isAcimaDoTeto();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        return CriterioLocalInativoStrategy.candidato(
                contexto,
                ClassificacaoAlocacaoLocal.INADEQUADO,
                String.format(
                        "Custo R$ %s excede o teto R$ %s informado para este evento.",
                        contexto.getCusto(),
                        contexto.getTeto()),
                true);
    }

    private static boolean agendaLiberada(ContextoAlocacaoLocal contexto) {
        return !contexto.isAvaliarAgenda() || contexto.isAgendaOk();
    }
}

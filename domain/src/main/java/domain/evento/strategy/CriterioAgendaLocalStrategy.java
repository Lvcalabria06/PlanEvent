package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioAgendaLocalStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return contexto.getLocal().isAtivo()
                && contexto.isAvaliarAgenda()
                && !contexto.isAgendaOk();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        return CriterioLocalInativoStrategy.candidato(
                contexto,
                ClassificacaoAlocacaoLocal.INDISPONIVEL,
                contexto.getMotivoAgenda(),
                false);
    }
}

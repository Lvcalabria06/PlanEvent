package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioLocalInativoStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return !contexto.getLocal().isAtivo();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        return candidato(
                contexto,
                ClassificacaoAlocacaoLocal.INADEQUADO,
                "Local inativo não pode ser opção válida.",
                true);
    }

    static CandidatoAnaliseLocal candidato(
            ContextoAlocacaoLocal contexto,
            ClassificacaoAlocacaoLocal classificacao,
            String justificativa,
            boolean agendaOk) {
        return new CandidatoAnaliseLocal(
                contexto.getLocal().getId(),
                contexto.getLocal().getNome(),
                classificacao,
                justificativa,
                contexto.getCusto(),
                contexto.getLocal().getCapacidade(),
                contexto.isAcimaDoTeto(),
                contexto.isCapacidadeOk(),
                agendaOk);
    }
}

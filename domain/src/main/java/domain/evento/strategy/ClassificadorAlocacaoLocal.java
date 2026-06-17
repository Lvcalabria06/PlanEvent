package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;

import java.util.List;

public class ClassificadorAlocacaoLocal {

    private final List<EstrategiaCriterioAlocacaoLocal> estrategias;

    public ClassificadorAlocacaoLocal(List<EstrategiaCriterioAlocacaoLocal> estrategias) {
        this.estrategias = List.copyOf(estrategias);
    }

    public CandidatoAnaliseLocal classificar(ContextoAlocacaoLocal contexto) {
        for (EstrategiaCriterioAlocacaoLocal estrategia : estrategias) {
            if (estrategia.aplicavel(contexto)) {
                return estrategia.avaliar(contexto);
            }
        }
        throw new IllegalStateException("Nenhuma estrategia concluiu a classificacao do local.");
    }
}

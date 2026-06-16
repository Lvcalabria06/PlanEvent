package domain.evento.strategy;

import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

public class CriterioInfraestruturaLocalStrategy implements EstrategiaCriterioAlocacaoLocal {

    @Override
    public boolean aplicavel(ContextoAlocacaoLocal contexto) {
        return contexto.getLocal().isAtivo()
                && agendaLiberada(contexto)
                && contexto.isCapacidadeOk()
                && !contexto.isAcimaDoTeto()
                && !contexto.isInfraOk();
    }

    @Override
    public CandidatoAnaliseLocal avaliar(ContextoAlocacaoLocal contexto) {
        String requisitos = contexto.getEvento().getRequisitosInfraestrutura();
        String infra = contexto.getLocal().getInfraestrutura() != null
                ? contexto.getLocal().getInfraestrutura()
                : "não informada";
        String justificativa = requisitos != null && !requisitos.isBlank()
                ? String.format(
                        "Requisitos do evento (%s) não estão cobertos pela infraestrutura do local (%s).",
                        requisitos.trim(),
                        infra)
                : "Infraestrutura do local não atende aos requisitos do evento.";
        return new CandidatoAnaliseLocal(
                contexto.getLocal().getId(),
                contexto.getLocal().getNome(),
                ClassificacaoAlocacaoLocal.VIAVEL_COM_RESSALVA,
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

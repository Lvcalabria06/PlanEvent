package domain.evento.strategy;

import domain.evento.entity.Evento;
import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.valueobject.ClassificacaoAlocacaoLocal;
import domain.local.entity.Local;

import java.math.BigDecimal;
import java.util.Locale;

public final class FabricaContextoAlocacaoLocal {

    private FabricaContextoAlocacaoLocal() {}

    public static ContextoAlocacaoLocal criar(
            Evento evento,
            Local local,
            BigDecimal teto,
            ConflitoAgendaLocalEvaluator conflitoEvaluator) {
        BigDecimal custo = local.getCusto() != null ? local.getCusto() : BigDecimal.ZERO;
        boolean capacidadeOk = local.getCapacidade() >= evento.getQuantidadeEstimadaParticipantes();
        boolean acimaDoTeto = custo.compareTo(teto) > 0;

        var ji = evento.getJanelaInicioPlanejamento();
        var jf = evento.getJanelaFimPlanejamento();
        boolean avaliarAgenda = ji != null && jf != null;
        boolean agendaOk = true;
        String motivoAgenda = "";
        if (avaliarAgenda) {
            var resultado = conflitoEvaluator.avaliar(local.getId(), evento.getId(), ji, jf);
            agendaOk = resultado.isOk();
            motivoAgenda = resultado.getMotivo();
        }

        boolean infraOk = requisitosInfraestruturaAtendidos(evento, local);

        return new ContextoAlocacaoLocal(
                evento,
                local,
                teto,
                custo,
                capacidadeOk,
                acimaDoTeto,
                avaliarAgenda,
                agendaOk,
                motivoAgenda,
                infraOk);
    }

    private static boolean requisitosInfraestruturaAtendidos(Evento evento, Local local) {
        String req = evento.getRequisitosInfraestrutura();
        if (req == null || req.isBlank()) {
            return true;
        }
        String infra = local.getInfraestrutura() != null ? local.getInfraestrutura() : "";
        String infraLower = infra.toLowerCase(Locale.ROOT);
        String[] partes = req.split(",");
        for (String p : partes) {
            String token = p.trim().toLowerCase(Locale.ROOT);
            if (token.isEmpty()) {
                continue;
            }
            if (!infraLower.contains(token)) {
                return false;
            }
        }
        return true;
    }
}

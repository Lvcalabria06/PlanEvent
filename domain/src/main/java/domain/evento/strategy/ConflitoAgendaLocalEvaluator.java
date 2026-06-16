package domain.evento.strategy;

import domain.local.entity.IndisponibilidadeLocal;
import domain.local.entity.ManutencaoLocal;
import domain.local.entity.ReservaLocal;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.util.IntervaloAgenda;

import java.time.LocalDateTime;
import java.util.List;

public class ConflitoAgendaLocalEvaluator {

    private final ReservaLocalRepository reservaLocalRepository;
    private final IndisponibilidadeLocalRepository indisponibilidadeLocalRepository;
    private final ManutencaoRepository manutencaoRepository;

    public ConflitoAgendaLocalEvaluator(
            ReservaLocalRepository reservaLocalRepository,
            IndisponibilidadeLocalRepository indisponibilidadeLocalRepository,
            ManutencaoRepository manutencaoRepository) {
        this.reservaLocalRepository = reservaLocalRepository;
        this.indisponibilidadeLocalRepository = indisponibilidadeLocalRepository;
        this.manutencaoRepository = manutencaoRepository;
    }

    public ResultadoConflitoAgenda avaliar(String localId, String eventoId, LocalDateTime ini, LocalDateTime fim) {
        IntervaloAgenda.validarFimAposInicio(ini, fim);
        for (IndisponibilidadeLocal ind : indisponibilidadeLocalRepository.listarPorLocalId(localId)) {
            if (IntervaloAgenda.seSobrepoe(ind.getDataInicio(), ind.getDataFim(), ini, fim)) {
                return ResultadoConflitoAgenda.indisponibilidade();
            }
        }
        List<ReservaLocal> reservas = reservaLocalRepository.buscarReservasPorLocalEPeriodo(localId, ini, fim);
        for (ReservaLocal r : reservas) {
            if (!r.getEventoId().equals(eventoId)) {
                return ResultadoConflitoAgenda.reserva();
            }
        }
        for (ManutencaoLocal m : manutencaoRepository.buscarPorLocalId(localId)) {
            if (IntervaloAgenda.seSobrepoe(m.getDataInicio(), m.getDataFim(), ini, fim)) {
                return ResultadoConflitoAgenda.manutencao();
            }
        }
        return ResultadoConflitoAgenda.semConflito();
    }

    public static final class ResultadoConflitoAgenda {
        private final boolean ok;
        private final String motivo;

        private ResultadoConflitoAgenda(boolean ok, String motivo) {
            this.ok = ok;
            this.motivo = motivo;
        }

        static ResultadoConflitoAgenda semConflito() {
            return new ResultadoConflitoAgenda(true, "");
        }

        static ResultadoConflitoAgenda indisponibilidade() {
            return new ResultadoConflitoAgenda(false,
                    "Conflito com bloqueio ou indisponibilidade na agenda do local.");
        }

        static ResultadoConflitoAgenda reserva() {
            return new ResultadoConflitoAgenda(false,
                    "Conflito com reserva existente na mesma janela.");
        }

        static ResultadoConflitoAgenda manutencao() {
            return new ResultadoConflitoAgenda(false,
                    "Conflito com manutenção agendada no local.");
        }

        public boolean isOk() {
            return ok;
        }

        public String getMotivo() {
            return motivo;
        }
    }
}

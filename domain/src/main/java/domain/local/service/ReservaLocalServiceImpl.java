package domain.local.service;

import domain.local.entity.AgendaLocal;
import domain.local.entity.IndisponibilidadeLocal;
import domain.local.entity.ReservaLocal;
import domain.local.repository.AgendaLocalRepository;
import domain.local.repository.IndisponibilidadeLocalRepository;
import domain.local.repository.LocalRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.util.IntervaloAgenda;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaLocalServiceImpl implements ReservaLocalService {

    private final ReservaLocalRepository reservaLocalRepository;
    private final IndisponibilidadeLocalRepository indisponibilidadeLocalRepository;
    private final AgendaLocalRepository agendaLocalRepository;
    private final LocalRepository localRepository;

    public ReservaLocalServiceImpl(
            ReservaLocalRepository reservaLocalRepository,
            IndisponibilidadeLocalRepository indisponibilidadeLocalRepository,
            AgendaLocalRepository agendaLocalRepository,
            LocalRepository localRepository) {
        this.reservaLocalRepository = reservaLocalRepository;
        this.indisponibilidadeLocalRepository = indisponibilidadeLocalRepository;
        this.agendaLocalRepository = agendaLocalRepository;
        this.localRepository = localRepository;
    }

    @Override
    public ReservaLocal reservar(String localId, String eventoId, LocalDateTime inicio, LocalDateTime fim) {
        if (eventoId == null || eventoId.isBlank()) {
            throw new IllegalArgumentException("O evento da reserva é obrigatório.");
        }

        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));

        IntervaloAgenda.validarFimAposInicio(inicio, fim);

        validarNaoBloqueadoPorIndisponibilidade(localId, inicio, fim);
        validarNaoBloqueadoPorReservasExistentes(localId, inicio, fim);

        String agendaId = obterOuCriarAgendaId(localId);
        ReservaLocal reserva = new ReservaLocal(agendaId, eventoId, inicio, fim);
        return reservaLocalRepository.salvar(reserva);
    }

    private void validarNaoBloqueadoPorIndisponibilidade(String localId, LocalDateTime inicio, LocalDateTime fim) {
        List<IndisponibilidadeLocal> itens = indisponibilidadeLocalRepository.listarPorLocalId(localId);
        for (IndisponibilidadeLocal ind : itens) {
            if (IntervaloAgenda.seSobrepoe(ind.getDataInicio(), ind.getDataFim(), inicio, fim)) {
                String motivo = ind.getMotivo() != null && !ind.getMotivo().isBlank() ? ind.getMotivo() : "horário indisponível (configuração do local).";
                throw new IllegalStateException("Reserva rejeitada: período indisponível para o local. Motivo: " + motivo);
            }
        }
    }

    private void validarNaoBloqueadoPorReservasExistentes(String localId, LocalDateTime inicio, LocalDateTime fim) {
        List<ReservaLocal> conflitos = reservaLocalRepository.buscarReservasPorLocalEPeriodo(localId, inicio, fim);
        if (!conflitos.isEmpty()) {
            throw new IllegalStateException("Reserva rejeitada: o intervalo conflita com outro agendamento já reservado neste local.");
        }
    }

    private String obterOuCriarAgendaId(String localId) {
        return agendaLocalRepository.buscarPorLocalId(localId)
                .orElseGet(() -> agendaLocalRepository.salvar(new AgendaLocal(localId)))
                .getId();
    }
}

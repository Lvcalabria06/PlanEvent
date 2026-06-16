package presentationbackend.scaffolding;

import domain.local.entity.AgendaLocal;
import domain.local.entity.ReservaLocal;
import domain.local.repository.AgendaLocalRepository;
import domain.local.repository.ReservaLocalRepository;
import domain.local.util.IntervaloAgenda;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryReservaLocalRepository implements ReservaLocalRepository {

    private final AgendaLocalRepository agendaLocalRepository;
    private final Map<String, ReservaLocal> porId = new ConcurrentHashMap<>();

    public InMemoryReservaLocalRepository(AgendaLocalRepository agendaLocalRepository) {
        this.agendaLocalRepository = agendaLocalRepository;
    }

    @Override
    public synchronized ReservaLocal salvar(ReservaLocal reserva) {
        porId.put(reserva.getId(), reserva);
        return reserva;
    }

    @Override
    public Optional<ReservaLocal> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<ReservaLocal> buscarReservasPorLocalEPeriodo(String localId, LocalDateTime inicio, LocalDateTime fim) {
        List<ReservaLocal> r = new ArrayList<>();
        for (ReservaLocal it : porId.values()) {
            Optional<AgendaLocal> ag = agendaLocalRepository.buscarPorId(it.getAgendaLocalId());
            if (ag.isEmpty() || !localId.equals(ag.get().getLocalId())) {
                continue;
            }
            if (IntervaloAgenda.seSobrepoe(it.getDataInicio(), it.getDataFim(), inicio, fim)) {
                r.add(it);
            }
        }
        return r;
    }
}

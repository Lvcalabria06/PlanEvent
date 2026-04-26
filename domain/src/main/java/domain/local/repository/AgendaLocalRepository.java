package domain.local.repository;

import domain.local.entity.AgendaLocal;

import java.util.Optional;

public interface AgendaLocalRepository {
    AgendaLocal salvar(AgendaLocal agenda);

    Optional<AgendaLocal> buscarPorId(String id);

    Optional<AgendaLocal> buscarPorLocalId(String localId);
}

package domain.local.repository;

import domain.local.entity.AvaliacaoContextualLocal;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoContextualLocalRepository {
    AvaliacaoContextualLocal salvar(AvaliacaoContextualLocal avaliacao);
    Optional<AvaliacaoContextualLocal> buscarPorId(String id);
    List<AvaliacaoContextualLocal> buscarPorLocalId(String localId);
    boolean existePorEventoIdELocalId(String eventoId, String localId);
}

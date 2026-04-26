package domain.local.repository;

import domain.local.entity.AvaliacaoLocalEvento;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoLocalEventoRepository {
    AvaliacaoLocalEvento salvar(AvaliacaoLocalEvento avaliacao);

    Optional<AvaliacaoLocalEvento> buscarPorEventoIdELocalId(String eventoId, String localId);

    List<AvaliacaoLocalEvento> listarPorLocalId(String localId);
}

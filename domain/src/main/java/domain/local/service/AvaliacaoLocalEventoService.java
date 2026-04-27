package domain.local.service;

import domain.local.entity.AvaliacaoLocalEvento;
import domain.local.valueobject.NivelAdequacao;

import java.util.List;

public interface AvaliacaoLocalEventoService {
    AvaliacaoLocalEvento registrarAvaliacao(
            String eventoId,
            String localId,
            NivelAdequacao nivel,
            String justificativa,
            String gestorId);

    List<AvaliacaoLocalEvento> listarAvaliacoesDoLocal(String localId);
}

package domain.local.service;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.AvaliacaoContextualLocal;

import java.util.List;
import java.util.Map;

public interface AvaliacaoContextualLocalService {
    AvaliacaoContextualLocal registrarAvaliacao(
            String eventoId,
            String localId,
            Map<String, Integer> notasPorCriterio,
            String justificativa,
            String usuarioResponsavel);

    ResumoDesempenhoContextualLocal consultarResumo(String localId, TipoEvento tipoEvento, PorteEvento porteEvento);

    List<AvaliacaoContextualLocal> listarHistorico(String localId);
}

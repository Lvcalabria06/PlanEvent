package domain.local.service;

import domain.local.entity.ManutencaoLocal;
import java.time.LocalDateTime;
import java.util.List;

public interface ManutencaoService {
    ManutencaoLocal cadastrarManutencao(String localId, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel);
    ManutencaoLocal editarManutencao(String id, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel);
    void removerManutencao(String id);
    List<ManutencaoLocal> listarManutencoesPorLocal(String localId);
}

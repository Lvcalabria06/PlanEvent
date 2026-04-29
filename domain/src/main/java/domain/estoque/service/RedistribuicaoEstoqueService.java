package domain.estoque.service;

import domain.estoque.entity.CenarioRedistribuicao;

import java.time.LocalDateTime;

public interface RedistribuicaoEstoqueService {

    CenarioRedistribuicao gerarCenarioRedistribuicao(String usuarioId,
                                                      LocalDateTime periodoInicio,
                                                      LocalDateTime periodoFim);

    CenarioRedistribuicao aplicarRedistribuicao(String cenarioId, String usuarioId);

    CenarioRedistribuicao invalidarCenario(String cenarioId, String usuarioId, String motivo);

    CenarioRedistribuicao buscarCenario(String cenarioId);
}

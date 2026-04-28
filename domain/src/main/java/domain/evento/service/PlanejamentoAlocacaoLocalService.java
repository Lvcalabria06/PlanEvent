package domain.evento.service;

import domain.evento.entity.Evento;
import domain.evento.planejamento.AlertaRiscoAlocacao;
import domain.evento.planejamento.ResultadoAnaliseAlocacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanejamentoAlocacaoLocalService {

    ResultadoAnaliseAlocacao analisarLocaisParaEvento(String eventoId, BigDecimal tetoCusto);

    Evento registrarParametrosPlanejamento(String eventoId, BigDecimal tetoCusto, LocalDateTime janelaInicio, LocalDateTime janelaFim);

    Evento fixarLocalPrincipal(String eventoId, String localId, BigDecimal tetoCusto);

    Evento registrarAlternativasContingencia(String eventoId, List<String> localIdsOrdenados);

    Optional<AlertaRiscoAlocacao> avaliarRiscoAlocacaoPrincipal(String eventoId);

    Evento executarTrocaPrincipalPorContingencia(String eventoId, String novoLocalId, String usuarioId, String motivo);
}

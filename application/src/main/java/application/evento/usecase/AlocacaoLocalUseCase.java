package application.evento.usecase;

import application.evento.dto.AlertaRiscoAlocacaoDto;
import application.evento.dto.EventoResponse;
import application.evento.dto.DefinirAlocacaoLocalRequest;
import application.evento.dto.FixarLocalPrincipalRequest;
import application.evento.dto.RegistrarAlternativasContingenciaRequest;
import application.evento.dto.RegistrarParametrosAlocacaoRequest;
import application.evento.dto.ResultadoAnaliseAlocacaoDto;
import application.evento.dto.TrocaLocalContingenciaRequest;
import application.evento.mapper.EventoDtoMapper;

import java.math.BigDecimal;
import java.util.Optional;

public interface AlocacaoLocalUseCase {

    ResultadoAnaliseAlocacaoDto analisarLocais(String eventoId, BigDecimal tetoCusto);

    EventoResponse registrarParametros(String eventoId, RegistrarParametrosAlocacaoRequest request);

    EventoResponse fixarLocalPrincipal(String eventoId, FixarLocalPrincipalRequest request);

    EventoResponse registrarAlternativas(String eventoId, RegistrarAlternativasContingenciaRequest request);

    Optional<AlertaRiscoAlocacaoDto> avaliarRisco(String eventoId);

    EventoResponse executarTrocaContingencia(String eventoId, TrocaLocalContingenciaRequest request);

    EventoResponse definirAlocacao(String eventoId, DefinirAlocacaoLocalRequest request);
}

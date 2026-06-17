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
import domain.evento.service.PlanejamentoAlocacaoLocalService;
import domain.local.repository.LocalRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class AlocacaoLocalUseCaseImpl implements AlocacaoLocalUseCase {

    private final PlanejamentoAlocacaoLocalService planejamentoService;
    private final LocalRepository localRepository;

    public AlocacaoLocalUseCaseImpl(
            PlanejamentoAlocacaoLocalService planejamentoService,
            LocalRepository localRepository) {
        this.planejamentoService = planejamentoService;
        this.localRepository = localRepository;
    }

    @Override
    public ResultadoAnaliseAlocacaoDto analisarLocais(String eventoId, BigDecimal tetoCusto) {
        return EventoDtoMapper.paraDto(planejamentoService.analisarLocaisParaEvento(eventoId, tetoCusto));
    }

    @Override
    public EventoResponse registrarParametros(String eventoId, RegistrarParametrosAlocacaoRequest request) {
        var evento = planejamentoService.registrarParametrosPlanejamento(
                eventoId,
                request.tetoCusto(),
                request.dataInicio(),
                request.dataFim());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public EventoResponse fixarLocalPrincipal(String eventoId, FixarLocalPrincipalRequest request) {
        var evento = planejamentoService.fixarLocalPrincipal(
                eventoId,
                request.localId(),
                request.tetoCusto());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public EventoResponse registrarAlternativas(String eventoId, RegistrarAlternativasContingenciaRequest request) {
        var evento = planejamentoService.registrarAlternativasContingencia(
                eventoId,
                request.localIdsOrdenados());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public Optional<AlertaRiscoAlocacaoDto> avaliarRisco(String eventoId) {
        return planejamentoService.avaliarRiscoAlocacaoPrincipal(eventoId)
                .map(alerta -> EventoDtoMapper.paraDto(alerta, localRepository));
    }

    @Override
    public EventoResponse executarTrocaContingencia(String eventoId, TrocaLocalContingenciaRequest request) {
        var evento = planejamentoService.executarTrocaPrincipalPorContingencia(
                eventoId,
                request.novoLocalId(),
                request.usuarioId(),
                request.motivo());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public EventoResponse definirAlocacao(String eventoId, DefinirAlocacaoLocalRequest request) {
        var evento = planejamentoService.fixarLocalPrincipal(
                eventoId,
                request.localPrincipalId(),
                request.tetoCusto());
        evento = planejamentoService.registrarAlternativasContingencia(
                eventoId,
                request.localIdsContingenciaOrdenados());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }
}

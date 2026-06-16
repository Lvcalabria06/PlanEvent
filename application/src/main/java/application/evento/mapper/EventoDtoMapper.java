package application.evento.mapper;

import application.evento.dto.AlertaRiscoAlocacaoDto;
import application.evento.dto.CandidatoAnaliseLocalDto;
import application.evento.dto.EventoResponse;
import application.evento.dto.ResultadoAnaliseAlocacaoDto;
import application.evento.dto.StatusAlocacaoEvento;
import application.evento.dto.TrocaLocalPlanejamentoDto;
import domain.evento.entity.Evento;
import domain.evento.planejamento.AlertaRiscoAlocacao;
import domain.evento.planejamento.CandidatoAnaliseLocal;
import domain.evento.planejamento.ResultadoAnaliseAlocacao;
import domain.evento.planejamento.TrocaLocalPlanejamento;
import domain.local.repository.LocalRepository;

import java.util.ArrayList;
import java.util.List;

public final class EventoDtoMapper {

    private EventoDtoMapper() {}

    public static EventoResponse paraResposta(Evento evento, LocalRepository localRepository) {
        String nomeLocalPrincipal = resolverNomeLocal(localRepository, evento.getLocalId());
        var localPrincipal = evento.getLocalId() != null
                ? localRepository.buscarPorId(evento.getLocalId()).orElse(null)
                : null;
        List<String> nomesContingencia = new ArrayList<>();
        for (String localId : evento.getLocaisContingenciaOrdenados()) {
            nomesContingencia.add(resolverNomeLocal(localRepository, localId));
        }
        List<TrocaLocalPlanejamentoDto> historico = evento.getHistoricoTrocasLocal().stream()
                .map(t -> paraDto(t, localRepository))
                .toList();
        StatusAlocacaoEvento status = calcularStatusAlocacao(evento);
        boolean podePlanejarLocal = !evento.isPlanejamentoConfirmado() && !evento.isCancelado();

        return new EventoResponse(
                evento.getId(),
                evento.getNome(),
                evento.getTipo(),
                evento.getPorte(),
                evento.getQuantidadeEstimadaParticipantes(),
                evento.getObjetivo(),
                evento.getLocalId(),
                nomeLocalPrincipal,
                localPrincipal != null ? localPrincipal.getCusto() : null,
                localPrincipal != null ? localPrincipal.getCapacidade() : null,
                evento.isPlanejamentoConfirmado(),
                evento.isConcluido(),
                evento.isCancelado(),
                evento.getJanelaInicioPlanejamento(),
                evento.getJanelaFimPlanejamento(),
                evento.getTetoCustoEspacoInformado(),
                evento.getRequisitosInfraestrutura(),
                evento.getLocaisContingenciaOrdenados(),
                nomesContingencia,
                status,
                podePlanejarLocal,
                historico,
                evento.getDataCriacao(),
                evento.getDataAtualizacao());
    }

    public static StatusAlocacaoEvento calcularStatusAlocacao(Evento evento) {
        if (evento.isConcluido()) {
            return StatusAlocacaoEvento.EVENTO_CONCLUIDO;
        }
        if (evento.getLocalId() == null || evento.getLocalId().isBlank()) {
            return StatusAlocacaoEvento.SEM_LOCAL_DEFINIDO;
        }
        if (!evento.isPlanejamentoConfirmado()) {
            return StatusAlocacaoEvento.LOCAL_DEFINIDO_PREPARACAO_PENDENTE;
        }
        return StatusAlocacaoEvento.PREPARACAO_CONFIRMADA_AGUARDANDO_RISCO;
    }

    private static TrocaLocalPlanejamentoDto paraDto(TrocaLocalPlanejamento troca, LocalRepository localRepository) {
        return new TrocaLocalPlanejamentoDto(
                troca.getDataHora(),
                troca.getUsuarioId(),
                troca.getMotivo(),
                troca.getLocalAnteriorId(),
                resolverNomeLocal(localRepository, troca.getLocalAnteriorId()),
                troca.getLocalNovoId(),
                resolverNomeLocal(localRepository, troca.getLocalNovoId()));
    }

    private static String resolverNomeLocal(LocalRepository localRepository, String localId) {
        if (localId == null || localId.isBlank()) {
            return null;
        }
        return localRepository.buscarPorId(localId)
                .map(local -> local.getNome())
                .orElse(null);
    }

    public static CandidatoAnaliseLocalDto paraDto(CandidatoAnaliseLocal candidato) {
        return new CandidatoAnaliseLocalDto(
                candidato.getLocalId(),
                candidato.getNomeLocal(),
                candidato.getClassificacao(),
                candidato.getJustificativa(),
                candidato.getCusto(),
                candidato.getCapacidade(),
                candidato.isAcimaDoTeto(),
                candidato.isCapacidadeOk(),
                candidato.isAgendaOk(),
                candidato.podeSerPrincipal());
    }

    public static ResultadoAnaliseAlocacaoDto paraDto(ResultadoAnaliseAlocacao resultado) {
        return new ResultadoAnaliseAlocacaoDto(
                resultado.getEventoId(),
                resultado.getCandidatos().stream().map(EventoDtoMapper::paraDto).toList());
    }

    public static AlertaRiscoAlocacaoDto paraDto(AlertaRiscoAlocacao alerta, LocalRepository localRepository) {
        return new AlertaRiscoAlocacaoDto(
                alerta.getEventoId(),
                alerta.getLocalPrincipalId(),
                alerta.getDescricao(),
                alerta.getMotivos(),
                alerta.getMelhorSubstitutoSugeridoId(),
                resolverNomeLocal(localRepository, alerta.getMelhorSubstitutoSugeridoId()));
    }

    public static AlertaRiscoAlocacaoDto paraDto(AlertaRiscoAlocacao alerta) {
        return new AlertaRiscoAlocacaoDto(
                alerta.getEventoId(),
                alerta.getLocalPrincipalId(),
                alerta.getDescricao(),
                alerta.getMotivos(),
                alerta.getMelhorSubstitutoSugeridoId(),
                null);
    }
}

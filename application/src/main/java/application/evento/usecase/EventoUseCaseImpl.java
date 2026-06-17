package application.evento.usecase;

import application.evento.dto.CriarEventoRequest;
import application.evento.dto.EditarEventoRequest;
import application.evento.dto.EventoResponse;
import application.evento.mapper.EventoDtoMapper;
import domain.estoque.service.PrevisaoConsumoService;
import domain.evento.entity.Evento;
import domain.evento.service.EventoService;
import domain.local.repository.LocalRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EventoUseCaseImpl implements EventoUseCase {

    private static final String USUARIO_SISTEMA = "gestor@empresa.com";

    private final EventoService eventoService;
    private final LocalRepository localRepository;
    private final PrevisaoConsumoService previsaoConsumoService;

    public EventoUseCaseImpl(EventoService eventoService, LocalRepository localRepository,
            PrevisaoConsumoService previsaoConsumoService) {
        this.eventoService = eventoService;
        this.localRepository = localRepository;
        this.previsaoConsumoService = previsaoConsumoService;
    }

    @Override
    public EventoResponse criar(CriarEventoRequest request) {
        validarPeriodoObrigatorio(request.dataInicio(), request.dataFim());
        Evento evento = eventoService.cadastrarEvento(
                request.nome(),
                request.tipo(),
                request.porte(),
                request.quantidadeEstimadaParticipantes(),
                request.objetivo(),
                request.dataInicio(),
                request.dataFim(),
                request.requisitosInfraestrutura());
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public EventoResponse editar(String id, EditarEventoRequest request) {
        validarPeriodoObrigatorio(request.dataInicio(), request.dataFim());
        Evento evento = eventoService.editarEvento(
                id,
                request.nome(),
                request.tipo(),
                request.porte(),
                request.quantidadeEstimadaParticipantes(),
                request.objetivo(),
                request.dataInicio(),
                request.dataFim(),
                request.requisitosInfraestrutura());
        previsaoConsumoService.tentarInvalidarPorAlteracaoEvento(id, USUARIO_SISTEMA);
        return EventoDtoMapper.paraResposta(evento, localRepository);
    }

    @Override
    public EventoResponse buscar(String id) {
        return EventoDtoMapper.paraResposta(eventoService.visualizarEvento(id), localRepository);
    }

    @Override
    public List<EventoResponse> listar() {
        return eventoService.listarEventos().stream()
                .map(evento -> EventoDtoMapper.paraResposta(evento, localRepository))
                .toList();
    }

    @Override
    public EventoResponse confirmarPreparacao(String id) {
        return EventoDtoMapper.paraResposta(eventoService.confirmarPreparacaoInicial(id), localRepository);
    }

    @Override
    public void cancelar(String id) {
        eventoService.cancelarEvento(id);
    }

    private void validarPeriodoObrigatorio(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Data de início e data de término são obrigatórias.");
        }
    }
}

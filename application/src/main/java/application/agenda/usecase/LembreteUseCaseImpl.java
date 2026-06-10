package application.agenda.usecase;

import application.agenda.dto.AlertaLembreteResponse;
import application.agenda.dto.CriarLembreteRequest;
import application.agenda.dto.EditarLembreteRequest;
import application.agenda.dto.LembreteResponse;
import application.agenda.mapper.AgendaDtoMapper;
import domain.agenda.entity.Compromisso;
import domain.agenda.entity.Lembrete;
import domain.agenda.port.AlertaLembreteMensagem;
import domain.agenda.service.CompromissoService;
import domain.agenda.service.LembreteService;

import java.time.LocalDateTime;
import java.util.List;

public class LembreteUseCaseImpl implements LembreteUseCase {

    private final LembreteService lembreteService;
    private final CompromissoService compromissoService;

    public LembreteUseCaseImpl(LembreteService lembreteService, CompromissoService compromissoService) {
        this.lembreteService = lembreteService;
        this.compromissoService = compromissoService;
    }

    @Override
    public LembreteResponse criar(CriarLembreteRequest request) {
        LocalDateTime inicioReferencia = resolverInicioReferencia(request.compromissoId(), request.eventoId());
        Lembrete novo = new Lembrete(
                request.compromissoId(),
                request.eventoId(),
                request.horario(),
                inicioReferencia);
        return AgendaDtoMapper.paraResposta(lembreteService.criarLembrete(novo));
    }

    @Override
    public LembreteResponse editar(String id, EditarLembreteRequest request) {
        Lembrete atual = lembreteService.buscarLembrete(id);
        Lembrete editado = Lembrete.reconstituir(
                atual.getId(),
                atual.getCompromissoId(),
                atual.getEventoId(),
                request.horario(),
                atual.isNotificado(),
                atual.getCreatedAt(),
                atual.getUpdatedAt());
        return AgendaDtoMapper.paraResposta(lembreteService.editarLembrete(editado));
    }

    @Override
    public void remover(String id) {
        lembreteService.removerLembrete(id);
    }

    @Override
    public LembreteResponse buscar(String id) {
        return AgendaDtoMapper.paraResposta(lembreteService.buscarLembrete(id));
    }

    @Override
    public List<LembreteResponse> listarPorCompromisso(String compromissoId) {
        return lembreteService.listarLembretesPorCompromisso(compromissoId).stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<LembreteResponse> listarPorEvento(String eventoId) {
        return lembreteService.listarLembretesPorEvento(eventoId).stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<LembreteResponse> listarPorGestor(String gestorId) {
        return lembreteService.listarLembretesPorGestor(gestorId).stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<LembreteResponse> listarTodos() {
        return lembreteService.listarTodosLembretes().stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public LembreteResponse dispararNotificacao(String id) {
        return AgendaDtoMapper.paraResposta(lembreteService.dispararNotificacao(id));
    }

    @Override
    public List<AlertaLembreteResponse> processarVencidos() {
        return lembreteService.processarLembretesVencidos().stream()
                .map(this::paraAlerta)
                .toList();
    }

    private AlertaLembreteResponse paraAlerta(Lembrete lembrete) {
        String titulo = null;
        if (lembrete.getCompromissoId() != null) {
            titulo = compromissoService.buscarCompromisso(lembrete.getCompromissoId()).getTitulo();
        }
        return new AlertaLembreteResponse(
                lembrete.getId(),
                AlertaLembreteMensagem.paraLembrete(lembrete, titulo),
                lembrete.getHorario());
    }

    private LocalDateTime resolverInicioReferencia(String compromissoId, String eventoId) {
        if (compromissoId != null && !compromissoId.isBlank()) {
            Compromisso compromisso = compromissoService.buscarCompromisso(compromissoId);
            return compromisso.getDataInicio();
        }
        return null;
    }
}

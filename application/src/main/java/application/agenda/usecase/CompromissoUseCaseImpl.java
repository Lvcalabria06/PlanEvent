package application.agenda.usecase;

import application.agenda.dto.CompromissoResponse;
import application.agenda.dto.CriarCompromissoRequest;
import application.agenda.dto.EditarCompromissoRequest;
import application.agenda.mapper.AgendaDtoMapper;
import domain.agenda.entity.Compromisso;
import domain.agenda.service.CompromissoService;

import java.util.List;

public class CompromissoUseCaseImpl implements CompromissoUseCase {

    private final CompromissoService compromissoService;

    public CompromissoUseCaseImpl(CompromissoService compromissoService) {
        this.compromissoService = compromissoService;
    }

    @Override
    public CompromissoResponse criar(CriarCompromissoRequest request) {
        Compromisso novo = new Compromisso(
                request.gestorId(),
                request.eventoId(),
                request.titulo(),
                request.descricao(),
                request.dataInicio(),
                request.dataFim());
        return AgendaDtoMapper.paraResposta(compromissoService.criarCompromisso(novo));
    }

    @Override
    public CompromissoResponse editar(String id, EditarCompromissoRequest request) {
        Compromisso atual = compromissoService.buscarCompromisso(id);
        Compromisso editado = Compromisso.reconstituir(
                atual.getId(),
                atual.getGestorId(),
                atual.getEventoId(),
                request.titulo(),
                request.descricao(),
                request.dataInicio(),
                request.dataFim(),
                atual.getStatus(),
                atual.getCreatedAt(),
                atual.getUpdatedAt());
        return AgendaDtoMapper.paraResposta(compromissoService.editarCompromisso(editado));
    }

    @Override
    public void remover(String id) {
        compromissoService.removerCompromisso(id);
    }

    @Override
    public CompromissoResponse buscar(String id) {
        return AgendaDtoMapper.paraResposta(compromissoService.buscarCompromisso(id));
    }

    @Override
    public List<CompromissoResponse> listarPorGestor(String gestorId) {
        return compromissoService.listarCompromissosPorGestor(gestorId).stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<CompromissoResponse> listarTodos() {
        return compromissoService.listarTodosCompromissos().stream()
                .map(AgendaDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public CompromissoResponse iniciar(String id) {
        return AgendaDtoMapper.paraResposta(compromissoService.iniciarCompromisso(id));
    }

    @Override
    public CompromissoResponse concluir(String id) {
        return AgendaDtoMapper.paraResposta(compromissoService.concluirCompromisso(id));
    }

    @Override
    public CompromissoResponse cancelar(String id) {
        return AgendaDtoMapper.paraResposta(compromissoService.cancelarCompromisso(id));
    }
}

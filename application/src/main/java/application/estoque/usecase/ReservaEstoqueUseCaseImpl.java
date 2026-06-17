package application.estoque.usecase;

import application.estoque.dto.AtualizarReservaEstoqueRequest;
import application.estoque.dto.CriarReservaEstoqueRequest;
import application.estoque.dto.ItemReservaRequest;
import application.estoque.dto.ReservaEstoqueResponse;
import application.estoque.mapper.EstoqueDtoMapper;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.service.ReservaEstoqueService;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaEstoqueUseCaseImpl implements ReservaEstoqueUseCase {

    private static final String RESERVA_ID_PLACEHOLDER = "temp";

    private final ReservaEstoqueService reservaEstoqueService;

    public ReservaEstoqueUseCaseImpl(ReservaEstoqueService reservaEstoqueService) {
        this.reservaEstoqueService = reservaEstoqueService;
    }

    @Override
    public ReservaEstoqueResponse criar(CriarReservaEstoqueRequest request) {
        ReservaEstoque reserva = reservaEstoqueService.criarReserva(
                request.eventoId(),
                request.dataInicio(),
                request.dataFim(),
                paraItensReserva(request.itens()));
        return EstoqueDtoMapper.paraResposta(reserva);
    }

    @Override
    public ReservaEstoqueResponse editar(String id, AtualizarReservaEstoqueRequest request) {
        ReservaEstoque reserva = reservaEstoqueService.atualizarReserva(
                id,
                request.dataInicio(),
                request.dataFim(),
                paraItensReserva(request.itens()));
        return EstoqueDtoMapper.paraResposta(reserva);
    }

    @Override
    public ReservaEstoqueResponse confirmar(String id) {
        return EstoqueDtoMapper.paraResposta(reservaEstoqueService.confirmar(id));
    }

    @Override
    public ReservaEstoqueResponse iniciarUso(String id) {
        return EstoqueDtoMapper.paraResposta(reservaEstoqueService.iniciarUso(id));
    }

    @Override
    public ReservaEstoqueResponse finalizar(String id) {
        return EstoqueDtoMapper.paraResposta(reservaEstoqueService.finalizar(id));
    }

    @Override
    public void cancelar(String id) {
        reservaEstoqueService.cancelar(id);
    }

    @Override
    public ReservaEstoqueResponse buscar(String id) {
        ReservaEstoque reserva = reservaEstoqueService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva de estoque nao encontrada."));
        return EstoqueDtoMapper.paraResposta(reserva);
    }

    @Override
    public List<ReservaEstoqueResponse> listarTodas() {
        return reservaEstoqueService.listarTodas().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<ReservaEstoqueResponse> listarPorEvento(String eventoId) {
        return reservaEstoqueService.listarPorEvento(eventoId).stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<ReservaEstoqueResponse> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservaEstoqueService.listarPorPeriodo(inicio, fim).stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    private List<ItemReserva> paraItensReserva(List<ItemReservaRequest> itens) {
        if (itens == null) {
            return List.of();
        }
        return itens.stream()
                .map(item -> new ItemReserva(RESERVA_ID_PLACEHOLDER, item.itemEstoqueId(), item.quantidade()))
                .toList();
    }
}

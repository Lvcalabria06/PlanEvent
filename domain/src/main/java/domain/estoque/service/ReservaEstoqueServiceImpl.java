package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.valueobject.StatusReservaEstoque;
import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservaEstoqueServiceImpl implements ReservaEstoqueService {

    private final ReservaEstoqueRepository reservaEstoqueRepository;
    private final EventoRepository eventoRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;

    public ReservaEstoqueServiceImpl(ReservaEstoqueRepository reservaEstoqueRepository,
                                     EventoRepository eventoRepository,
                                     ItemEstoqueRepository itemEstoqueRepository) {
        this.reservaEstoqueRepository = reservaEstoqueRepository;
        this.eventoRepository = eventoRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
    }

    @Override
    public ReservaEstoque criarReserva(String eventoId,
                                        LocalDateTime dataInicio,
                                        LocalDateTime dataFim,
                                        List<ItemReserva> itensReservados) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado para reserva de estoque."));
        if (evento.isConcluido()) {
            throw new IllegalStateException("Nao e possivel criar reserva para evento ja concluido.");
        }

        validarItensReservados(itensReservados);

        ReservaEstoque reserva = new ReservaEstoque(eventoId, dataInicio, dataFim, itensReservados);
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque atualizarReserva(String reservaId,
                                            LocalDateTime dataInicio,
                                            LocalDateTime dataFim,
                                            List<ItemReserva> itensReservados) {
        ReservaEstoque reserva = exigirReserva(reservaId);
        if (reserva.getStatus() == StatusReservaEstoque.FINALIZADA
                || reserva.getStatus() == StatusReservaEstoque.CANCELADA) {
            throw new IllegalStateException("Reservas finalizadas ou canceladas nao podem ser atualizadas.");
        }
        validarItensReservados(itensReservados);
        reserva.atualizarSolicitacao(dataInicio, dataFim, itensReservados);
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque confirmar(String reservaId) {
        ReservaEstoque reserva = exigirReserva(reservaId);
        reserva.confirmar();
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque iniciarUso(String reservaId) {
        ReservaEstoque reserva = exigirReserva(reservaId);
        reserva.iniciarUso();
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque finalizar(String reservaId) {
        ReservaEstoque reserva = exigirReserva(reservaId);
        reserva.finalizar();
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public void cancelar(String reservaId) {
        ReservaEstoque reserva = exigirReserva(reservaId);
        reserva.cancelar();
        reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public Optional<ReservaEstoque> buscarPorId(String reservaId) {
        return reservaEstoqueRepository.buscarPorId(reservaId);
    }

    @Override
    public List<ReservaEstoque> listarPorEvento(String eventoId) {
        return reservaEstoqueRepository.listarPorEvento(eventoId);
    }

    @Override
    public List<ReservaEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservaEstoqueRepository.listarPorPeriodo(inicio, fim);
    }

    @Override
    public List<ReservaEstoque> listarTodas() {
        return reservaEstoqueRepository.listarTodas();
    }

    private ReservaEstoque exigirReserva(String reservaId) {
        return reservaEstoqueRepository.buscarPorId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva de estoque nao encontrada."));
    }

    private void validarItensReservados(List<ItemReserva> itensReservados) {
        if (itensReservados == null || itensReservados.isEmpty()) {
            throw new IllegalArgumentException("Reserva deve possuir ao menos um item.");
        }
        for (ItemReserva itemReserva : itensReservados) {
            Optional<ItemEstoque> itemEstoque = itemEstoqueRepository.buscarPorId(itemReserva.getItemEstoqueId());
            if (itemEstoque.isEmpty()) {
                throw new IllegalArgumentException(
                        "Item de estoque nao encontrado: " + itemReserva.getItemEstoqueId());
            }
            if (!itemEstoque.get().isAtivo()) {
                throw new IllegalStateException(
                        "Item de estoque inativo nao pode ser reservado: " + itemReserva.getItemEstoqueId());
            }
        }
    }
}

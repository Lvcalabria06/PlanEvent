package domain.estoque.service;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.repository.ItemEstoqueRepository;
import domain.estoque.repository.ReservaEstoqueRepository;
import domain.estoque.valueobject.DetalheConflitoReserva;
import domain.estoque.valueobject.ResultadoDisponibilidadeReserva;
import domain.estoque.valueobject.StatusReservaEstoque;
import domain.evento.repository.EventoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaEstoqueServiceImpl implements ReservaEstoqueService {

    private final ReservaEstoqueRepository reservaEstoqueRepository;
    private final ItemEstoqueRepository itemEstoqueRepository;
    private final EventoRepository eventoRepository;

    public ReservaEstoqueServiceImpl(ReservaEstoqueRepository reservaEstoqueRepository,
                                     ItemEstoqueRepository itemEstoqueRepository,
                                     EventoRepository eventoRepository) {
        this.reservaEstoqueRepository = reservaEstoqueRepository;
        this.itemEstoqueRepository = itemEstoqueRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public ResultadoDisponibilidadeReserva verificarDisponibilidade(String eventoId,
                                                                    LocalDateTime dataInicio,
                                                                    LocalDateTime dataFim,
                                                                    List<ItemReserva> itensSolicitados) {
        validarEventoExistente(eventoId);
        validarSolicitacao(dataInicio, dataFim, itensSolicitados);
        return calcularDisponibilidade(dataInicio, dataFim, itensSolicitados, Optional.empty());
    }

    @Override
    public ReservaEstoque criarReserva(String eventoId,
                                       LocalDateTime dataInicio,
                                       LocalDateTime dataFim,
                                       List<ItemReserva> itensSolicitados) {
        ResultadoDisponibilidadeReserva resultado = verificarDisponibilidade(eventoId, dataInicio, dataFim, itensSolicitados);
        if (resultado.isPossuiConflito()) {
            throw new IllegalStateException("Reserva possui conflito de disponibilidade.");
        }
        ReservaEstoque reserva = new ReservaEstoque(eventoId, dataInicio, dataFim, itensSolicitados);
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque confirmarReserva(String reservaId, String justificativa, boolean autorizada) {
        ReservaEstoque reserva = buscarReservaExistente(reservaId);
        ResultadoDisponibilidadeReserva resultado = calcularDisponibilidade(
                reserva.getDataInicio(),
                reserva.getDataFim(),
                reserva.getItensReservados(),
                Optional.of(reserva.getId())
        );

        if (resultado.isPossuiConflito() && (!autorizada || justificativa == null || justificativa.isBlank())) {
            throw new IllegalStateException("Nao e permitido confirmar reserva acima da disponibilidade real sem autorizacao.");
        }

        reserva.confirmar();
        return reservaEstoqueRepository.salvar(reserva);
    }

    @Override
    public ReservaEstoque atualizarSolicitacao(String reservaId,
                                               LocalDateTime novaDataInicio,
                                               LocalDateTime novaDataFim,
                                               List<ItemReserva> novosItens) {
        ReservaEstoque reserva = buscarReservaExistente(reservaId);
        validarSolicitacao(novaDataInicio, novaDataFim, novosItens);
        reserva.atualizarSolicitacao(novaDataInicio, novaDataFim, novosItens);
        return reservaEstoqueRepository.salvar(reserva);
    }

    private ResultadoDisponibilidadeReserva calcularDisponibilidade(LocalDateTime dataInicio,
                                                                    LocalDateTime dataFim,
                                                                    List<ItemReserva> itensSolicitados,
                                                                    Optional<String> reservaIgnoradaId) {
        List<DetalheConflitoReserva> conflitos = new ArrayList<>();

        for (ItemReserva itemSolicitado : itensSolicitados) {
            ItemEstoque itemEstoque = itemEstoqueRepository.buscarPorId(itemSolicitado.getItemEstoqueId())
                    .orElseThrow(() -> new IllegalArgumentException("Item de estoque nao encontrado."));

            int quantidadeReservada = 0;
            List<String> eventosEmConflito = new ArrayList<>();

            for (ReservaEstoque reservaExistente : reservaEstoqueRepository.listarTodas()) {
                if (reservaIgnoradaId.isPresent() && reservaIgnoradaId.get().equals(reservaExistente.getId())) {
                    continue;
                }
                if (!statusConsiderado(reservaExistente.getStatus())) {
                    continue;
                }
                if (!reservaExistente.sobrepoePeriodo(dataInicio, dataFim)) {
                    continue;
                }

                for (ItemReserva itemReservado : reservaExistente.getItensReservados()) {
                    if (itemReservado.getItemEstoqueId().equals(itemSolicitado.getItemEstoqueId())) {
                        quantidadeReservada += itemReservado.getQuantidade();
                        eventosEmConflito.add(reservaExistente.getEventoId());
                    }
                }
            }

            int disponibilidadeReal = itemEstoque.getQuantidadeTotal() - quantidadeReservada;

            if (itemSolicitado.getQuantidade() > disponibilidadeReal) {
                conflitos.add(new DetalheConflitoReserva(
                        itemSolicitado.getItemEstoqueId(),
                        itemSolicitado.getQuantidade(),
                        Math.max(disponibilidadeReal, 0),
                        quantidadeReservada,
                        eventosEmConflito
                ));
            }
        }

        return new ResultadoDisponibilidadeReserva(conflitos);
    }

    private boolean statusConsiderado(StatusReservaEstoque status) {
        return status == StatusReservaEstoque.PENDENTE
                || status == StatusReservaEstoque.CONFIRMADA
                || status == StatusReservaEstoque.EM_USO;
    }

    private void validarEventoExistente(String eventoId) {
        eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado."));
    }

    private void validarSolicitacao(LocalDateTime dataInicio, LocalDateTime dataFim, List<ItemReserva> itensSolicitados) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Periodo da reserva invalido.");
        }
        if (itensSolicitados == null || itensSolicitados.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um item para reserva.");
        }
    }

    private ReservaEstoque buscarReservaExistente(String reservaId) {
        return reservaEstoqueRepository.buscarPorId(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada."));
    }
}

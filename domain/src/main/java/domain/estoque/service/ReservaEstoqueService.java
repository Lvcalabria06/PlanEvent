package domain.estoque.service;

import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaEstoqueService {

    ReservaEstoque criarReserva(String eventoId,
                                LocalDateTime dataInicio,
                                LocalDateTime dataFim,
                                List<ItemReserva> itensReservados);

    ReservaEstoque atualizarReserva(String reservaId,
                                    LocalDateTime dataInicio,
                                    LocalDateTime dataFim,
                                    List<ItemReserva> itensReservados);

    ReservaEstoque confirmar(String reservaId);

    ReservaEstoque iniciarUso(String reservaId);

    ReservaEstoque finalizar(String reservaId);

    void cancelar(String reservaId);

    Optional<ReservaEstoque> buscarPorId(String reservaId);

    List<ReservaEstoque> listarPorEvento(String eventoId);

    List<ReservaEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<ReservaEstoque> listarTodas();
}

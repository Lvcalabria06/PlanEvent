package domain.estoque.service;

import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ReservaEstoque;
import domain.estoque.valueobject.ResultadoDisponibilidadeReserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaEstoqueService {
    ResultadoDisponibilidadeReserva verificarDisponibilidade(String eventoId,
                                                             LocalDateTime dataInicio,
                                                             LocalDateTime dataFim,
                                                             List<ItemReserva> itensSolicitados);

    ReservaEstoque criarReserva(String eventoId,
                                LocalDateTime dataInicio,
                                LocalDateTime dataFim,
                                List<ItemReserva> itensSolicitados);

    ReservaEstoque confirmarReserva(String reservaId, String justificativa, boolean autorizada);

    ReservaEstoque atualizarSolicitacao(String reservaId,
                                        LocalDateTime novaDataInicio,
                                        LocalDateTime novaDataFim,
                                        List<ItemReserva> novosItens);
}

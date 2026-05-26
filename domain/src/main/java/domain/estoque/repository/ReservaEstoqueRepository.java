package domain.estoque.repository;

import domain.estoque.entity.ReservaEstoque;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaEstoqueRepository {
    ReservaEstoque salvar(ReservaEstoque reservaEstoque);
    Optional<ReservaEstoque> buscarPorId(String id);
    List<ReservaEstoque> listarTodas();
    List<ReservaEstoque> listarPorEvento(String eventoId);
    List<ReservaEstoque> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim);
}

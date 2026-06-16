package domain.financeiro.repository;

import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;

import java.util.Optional;

public interface SimulacaoRelatorioRepository {

    SimulacaoRelatorioFinanceiro salvar(SimulacaoRelatorioFinanceiro simulacao);

    Optional<SimulacaoRelatorioFinanceiro> buscarPorId(String id);

    void remover(String id);
}

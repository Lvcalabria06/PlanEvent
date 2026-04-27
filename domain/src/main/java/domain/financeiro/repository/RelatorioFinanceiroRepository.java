package domain.financeiro.repository;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.util.List;
import java.util.Optional;

public interface RelatorioFinanceiroRepository {

    RelatorioFinanceiro salvar(RelatorioFinanceiro relatorio);

    Optional<RelatorioFinanceiro> buscarPorId(String id);

    List<RelatorioFinanceiro> listarPorEventoId(String eventoId);
}

package domain.financeiro.repository;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.util.List;
import java.util.Optional;

public interface RelatorioFinanceiroRepository {

    /**
     * RN3: Persiste o relatório gerado com data e usuário responsável.
     * RN6: Permite múltiplos relatórios por evento.
     */
    RelatorioFinanceiro salvar(RelatorioFinanceiro relatorio);

    Optional<RelatorioFinanceiro> buscarPorId(String id);

    /**
     * RN6: Lista todos os relatórios gerados para um evento,
     * cada um identificado individualmente.
     */
    List<RelatorioFinanceiro> listarPorEventoId(String eventoId);
}

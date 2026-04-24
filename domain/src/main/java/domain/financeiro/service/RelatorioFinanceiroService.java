package domain.financeiro.service;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.util.List;

public interface RelatorioFinanceiroService {

    /**
     * RN1: Busca dados do banco via repositórios (JPQL na camada de infra).
     * RN2: Consolida total geral, total por categoria e percentual de variação.
     * RN3: Persiste com data de geração e usuário responsável.
     * RN4: Impede geração se não houver orçamento previsto por categoria.
     * RN5: O relatório reflete o estado atual no momento da geração.
     * RN6: Permite múltiplos relatórios por evento.
     * RN8: Calcula percentual via ((realizado - previsto) / previsto) × 100.
     */
    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId);

    RelatorioFinanceiro buscarRelatorio(String id);

    /**
     * RN6: Lista todos os relatórios do evento, cada um identificado individualmente.
     */
    List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId);
}

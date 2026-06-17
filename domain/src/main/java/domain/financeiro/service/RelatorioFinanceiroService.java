package domain.financeiro.service;

import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.valueobject.ComparativoRelatorioPar;
import domain.financeiro.valueobject.ParametrosCenarioSimulacao;
import domain.financeiro.valueobject.TipoRelatorio;

import java.util.List;

public interface RelatorioFinanceiroService {

    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId);

    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId, TipoRelatorio tipo);

    RelatorioFinanceiro gerarRelatorioOficial(String eventoId, String usuarioId, String motivoNovaVersaoOficial);

    SimulacaoRelatorioFinanceiro simularRelatorio(String eventoId, String usuarioId);

    /** Simulação what-if com parâmetros de cenário (RN15). */
    SimulacaoRelatorioFinanceiro simularRelatorioComCenario(String eventoId,
                                                             String usuarioId,
                                                             ParametrosCenarioSimulacao parametros);

    RelatorioFinanceiro confirmarGeracao(String simulacaoId, TipoRelatorio tipo, String motivoNovaVersaoOficial);

    RelatorioFinanceiro buscarRelatorio(String id);

    List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId);

    /** Comparação entre dois snapshots escolhidos pelo usuário (RN17). */
    ComparativoRelatorioPar compararRelatorios(String relatorioBaseId, String relatorioComparadoId);
}

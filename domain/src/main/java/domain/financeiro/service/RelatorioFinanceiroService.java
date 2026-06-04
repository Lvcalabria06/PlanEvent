package domain.financeiro.service;

import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.valueobject.TipoRelatorio;

import java.util.List;

public interface RelatorioFinanceiroService {

    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId);

    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId, TipoRelatorio tipo);

    RelatorioFinanceiro gerarRelatorioOficial(String eventoId, String usuarioId, String motivoNovaVersaoOficial);

    SimulacaoRelatorioFinanceiro simularRelatorio(String eventoId, String usuarioId);

    RelatorioFinanceiro confirmarGeracao(String simulacaoId, TipoRelatorio tipo, String motivoNovaVersaoOficial);

    RelatorioFinanceiro buscarRelatorio(String id);

    List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId);
}

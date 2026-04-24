package domain.financeiro.service;

import domain.financeiro.entity.RelatorioFinanceiro;

import java.util.List;

public interface RelatorioFinanceiroService {


    RelatorioFinanceiro gerarRelatorio(String eventoId, String usuarioId);

    RelatorioFinanceiro buscarRelatorio(String id);


    List<RelatorioFinanceiro> listarRelatoriosPorEvento(String eventoId);
}

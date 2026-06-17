package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;

import java.util.List;

public interface RelatorioFinanceiroUseCase {

    List<FinanceiroDtos.RelatorioDto> listar(String eventoId);

    FinanceiroDtos.RelatorioDto buscar(String relatorioId);

    FinanceiroDtos.SimulacaoDto simular(String eventoId, String usuarioId);

    /** Simulação what-if com parâmetros de cenário (RN15). */
    FinanceiroDtos.SimulacaoDto simularWhatIf(String eventoId, String usuarioId,
                                               FinanceiroDtos.SimularWhatIfRequest request);

    FinanceiroDtos.RelatorioDto confirmar(String simulacaoId, FinanceiroDtos.ConfirmarRelatorioRequest request);

    FinanceiroDtos.RelatorioDto gerarPreliminar(String eventoId, String usuarioId);

    FinanceiroDtos.RelatorioDto gerarOficial(String eventoId, FinanceiroDtos.GerarOficialRequest request, String usuarioId);

    /** Comparação entre dois snapshots (RN17). */
    FinanceiroDtos.ComparativoRelatorioParDto compararRelatorios(String relatorioBaseId,
                                                                  String relatorioComparadoId);
}

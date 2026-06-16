package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.service.RelatorioFinanceiroService;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.ParametrosCenarioSimulacao;
import domain.financeiro.valueobject.TipoRelatorio;

import java.util.List;

public class RelatorioFinanceiroUseCaseImpl implements RelatorioFinanceiroUseCase {

    private final RelatorioFinanceiroService relatorioService;

    public RelatorioFinanceiroUseCaseImpl(RelatorioFinanceiroService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @Override
    public List<FinanceiroDtos.RelatorioDto> listar(String eventoId) {
        return relatorioService.listarRelatoriosPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toRelatorioDto)
                .toList();
    }

    @Override
    public FinanceiroDtos.RelatorioDto buscar(String relatorioId) {
        return FinanceiroMapper.toRelatorioDto(relatorioService.buscarRelatorio(relatorioId));
    }

    @Override
    public FinanceiroDtos.SimulacaoDto simular(String eventoId, String usuarioId) {
        SimulacaoRelatorioFinanceiro simulacao = relatorioService.simularRelatorio(eventoId, usuarioId);
        return FinanceiroMapper.toSimulacaoDto(simulacao);
    }

    @Override
    public FinanceiroDtos.SimulacaoDto simularWhatIf(String eventoId, String usuarioId,
                                                      FinanceiroDtos.SimularWhatIfRequest request) {
        List<ParametrosCenarioSimulacao.DespesaHipotetica> hipoteticas = request.despesasHipoteticas() == null
                ? List.of()
                : request.despesasHipoteticas().stream()
                        .map(h -> new ParametrosCenarioSimulacao.DespesaHipotetica(
                                CategoriaDespesa.valueOf(h.categoria()), h.valor()))
                        .toList();

        ParametrosCenarioSimulacao parametros = new ParametrosCenarioSimulacao(
                request.incluirPendentes(),
                request.cenarioPessimistaCobertura(),
                hipoteticas);

        SimulacaoRelatorioFinanceiro simulacao = relatorioService
                .simularRelatorioComCenario(eventoId, usuarioId, parametros);
        return FinanceiroMapper.toSimulacaoDto(simulacao);
    }

    @Override
    public FinanceiroDtos.RelatorioDto confirmar(String simulacaoId, FinanceiroDtos.ConfirmarRelatorioRequest request) {
        TipoRelatorio tipo = TipoRelatorio.valueOf(request.tipo());
        RelatorioFinanceiro relatorio = relatorioService.confirmarGeracao(
                simulacaoId, tipo, request.motivoNovaVersaoOficial());
        return FinanceiroMapper.toRelatorioDto(relatorio);
    }

    @Override
    public FinanceiroDtos.RelatorioDto gerarPreliminar(String eventoId, String usuarioId) {
        return FinanceiroMapper.toRelatorioDto(
                relatorioService.gerarRelatorio(eventoId, usuarioId, TipoRelatorio.PRELIMINAR));
    }

    @Override
    public FinanceiroDtos.RelatorioDto gerarOficial(String eventoId, FinanceiroDtos.GerarOficialRequest request, String usuarioId) {
        String motivo = request != null ? request.motivoNovaVersaoOficial() : null;
        return FinanceiroMapper.toRelatorioDto(
                relatorioService.gerarRelatorioOficial(eventoId, usuarioId, motivo));
    }

    @Override
    public FinanceiroDtos.ComparativoRelatorioParDto compararRelatorios(String relatorioBaseId,
                                                                         String relatorioComparadoId) {
        return FinanceiroMapper.toComparativoParDto(
                relatorioService.compararRelatorios(relatorioBaseId, relatorioComparadoId));
    }
}

package application.conciliacao.usecase;

import application.conciliacao.dto.DespesaResumoResponse;
import application.conciliacao.dto.ExecutarConciliacaoRequest;
import application.conciliacao.dto.GerarRelatorioRequest;
import application.conciliacao.dto.RelatorioConciliacaoResponse;
import application.conciliacao.dto.VincularManualmenteRequest;
import application.conciliacao.dto.VinculoConciliacaoResponse;
import application.conciliacao.mapper.ConciliacaoDtoMapper;
import application.contrato.dto.ContratoResponse;
import application.contrato.mapper.ContratoDtoMapper;
import domain.conciliacao.service.ConciliacaoService;

import java.util.List;

public class ConciliacaoUseCaseImpl implements ConciliacaoUseCase {

    private final ConciliacaoService conciliacaoService;

    public ConciliacaoUseCaseImpl(ConciliacaoService conciliacaoService) {
        this.conciliacaoService = conciliacaoService;
    }

    @Override
    public void executarConciliacaoAutomatica(ExecutarConciliacaoRequest request) {
        conciliacaoService.executarConciliacaoAutomatica(request.eventoId(), request.responsavelId());
    }

    @Override
    public VinculoConciliacaoResponse vincularManualmente(VincularManualmenteRequest request) {
        var vinculo = conciliacaoService.vincularManualmente(
                request.despesaId(), request.contratoId(), request.responsavelId());
        return ConciliacaoDtoMapper.paraResposta(vinculo);
    }

    @Override
    public List<DespesaResumoResponse> listarDespesasDescobertasPorEvento(String eventoId) {
        return conciliacaoService.listarDespesasDescobertasPorEvento(eventoId).stream()
                .map(ConciliacaoDtoMapper::paraResumo)
                .toList();
    }

    @Override
    public List<ContratoResponse> listarContratosExtrapoladosPorEvento(String eventoId) {
        return conciliacaoService.listarContratosExtrapoladosPorEvento(eventoId).stream()
                .map(ContratoDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<VinculoConciliacaoResponse> listarVinculosPorEvento(String eventoId) {
        return conciliacaoService.listarVinculosPorEvento(eventoId).stream()
                .map(ConciliacaoDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public RelatorioConciliacaoResponse gerarRelatorio(String eventoId, GerarRelatorioRequest request) {
        var relatorio = conciliacaoService.gerarRelatorio(eventoId, request.responsavelId());
        return ConciliacaoDtoMapper.paraResposta(relatorio);
    }
}

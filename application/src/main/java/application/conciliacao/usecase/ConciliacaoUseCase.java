package application.conciliacao.usecase;

import application.conciliacao.dto.DespesaResumoResponse;
import application.conciliacao.dto.ExecutarConciliacaoRequest;
import application.conciliacao.dto.GerarRelatorioRequest;
import application.conciliacao.dto.RelatorioConciliacaoResponse;
import application.conciliacao.dto.VincularManualmenteRequest;
import application.conciliacao.dto.VinculoConciliacaoResponse;
import application.contrato.dto.ContratoResponse;

import java.util.List;

public interface ConciliacaoUseCase {

    void executarConciliacaoAutomatica(ExecutarConciliacaoRequest request);

    VinculoConciliacaoResponse vincularManualmente(VincularManualmenteRequest request);

    List<DespesaResumoResponse> listarDespesasDescobertasPorEvento(String eventoId);

    List<ContratoResponse> listarContratosExtrapoladosPorEvento(String eventoId);

    List<VinculoConciliacaoResponse> listarVinculosPorEvento(String eventoId);

    RelatorioConciliacaoResponse gerarRelatorio(String eventoId, GerarRelatorioRequest request);
}

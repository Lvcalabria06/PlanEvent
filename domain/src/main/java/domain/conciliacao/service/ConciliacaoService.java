package domain.conciliacao.service;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.entity.VinculoConciliacao;
import domain.contrato.entity.Contrato;
import domain.financeiro.entity.Despesa;

import java.util.List;

public interface ConciliacaoService {

    void executarConciliacaoAutomatica(String eventoId, String responsavelId);

    List<Despesa> listarDespesasDescobertasPorEvento(String eventoId);

    List<Contrato> listarContratosExtrapoladosPorEvento(String eventoId);

    VinculoConciliacao vincularManualmente(String despesaId, String contratoId, String responsavelId);

    RelatorioConciliacao gerarRelatorio(String eventoId, String responsavelId);
}

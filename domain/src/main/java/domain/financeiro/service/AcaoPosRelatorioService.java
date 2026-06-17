package domain.financeiro.service;

import domain.financeiro.entity.AcaoPosRelatorio;
import domain.financeiro.valueobject.TipoRecomendacaoFinanceira;

import java.util.List;

public interface AcaoPosRelatorioService {

    AcaoPosRelatorio registrarAcao(String relatorioId,
                                    TipoRecomendacaoFinanceira tipoRecomendacao,
                                    String descricao);

    AcaoPosRelatorio marcarComoTratada(String acaoId);

    List<AcaoPosRelatorio> listarPorRelatorio(String relatorioId);

    AcaoPosRelatorio buscarPorId(String acaoId);
}

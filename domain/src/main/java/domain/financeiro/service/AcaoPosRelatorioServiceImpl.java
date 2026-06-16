package domain.financeiro.service;

import domain.financeiro.entity.AcaoPosRelatorio;
import domain.financeiro.repository.AcaoPosRelatorioRepository;
import domain.financeiro.repository.RelatorioFinanceiroRepository;
import domain.financeiro.valueobject.TipoRecomendacaoFinanceira;

import java.util.List;

public class AcaoPosRelatorioServiceImpl implements AcaoPosRelatorioService {

    private final AcaoPosRelatorioRepository acaoRepository;
    private final RelatorioFinanceiroRepository relatorioRepository;

    public AcaoPosRelatorioServiceImpl(AcaoPosRelatorioRepository acaoRepository,
                                        RelatorioFinanceiroRepository relatorioRepository) {
        this.acaoRepository = acaoRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Override
    public AcaoPosRelatorio registrarAcao(String relatorioId,
                                           TipoRecomendacaoFinanceira tipoRecomendacao,
                                           String descricao) {
        relatorioRepository.buscarPorId(relatorioId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório não encontrado."));
        AcaoPosRelatorio acao = new AcaoPosRelatorio(relatorioId, tipoRecomendacao, descricao);
        return acaoRepository.salvar(acao);
    }

    @Override
    public AcaoPosRelatorio marcarComoTratada(String acaoId) {
        AcaoPosRelatorio acao = buscarPorId(acaoId);
        acao.marcarComoTratada();
        return acaoRepository.salvar(acao);
    }

    @Override
    public List<AcaoPosRelatorio> listarPorRelatorio(String relatorioId) {
        return acaoRepository.listarPorRelatorioId(relatorioId);
    }

    @Override
    public AcaoPosRelatorio buscarPorId(String acaoId) {
        return acaoRepository.buscarPorId(acaoId)
                .orElseThrow(() -> new IllegalArgumentException("Ação pós-relatório não encontrada."));
    }
}

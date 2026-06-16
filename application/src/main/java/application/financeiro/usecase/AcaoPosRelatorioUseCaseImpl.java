package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.financeiro.service.AcaoPosRelatorioService;
import domain.financeiro.valueobject.TipoRecomendacaoFinanceira;

import java.util.List;

public class AcaoPosRelatorioUseCaseImpl implements AcaoPosRelatorioUseCase {

    private final AcaoPosRelatorioService service;

    public AcaoPosRelatorioUseCaseImpl(AcaoPosRelatorioService service) {
        this.service = service;
    }

    @Override
    public FinanceiroDtos.AcaoPosRelatorioDto registrar(
            String relatorioId,
            FinanceiroDtos.RegistrarAcaoPosRelatorioRequest request) {
        return FinanceiroMapper.toAcaoPosRelatorioDto(
                service.registrarAcao(
                        relatorioId,
                        TipoRecomendacaoFinanceira.valueOf(request.tipoRecomendacao()),
                        request.descricao()));
    }

    @Override
    public FinanceiroDtos.AcaoPosRelatorioDto marcarComoTratada(String acaoId) {
        return FinanceiroMapper.toAcaoPosRelatorioDto(service.marcarComoTratada(acaoId));
    }

    @Override
    public List<FinanceiroDtos.AcaoPosRelatorioDto> listarPorRelatorio(String relatorioId) {
        return service.listarPorRelatorio(relatorioId).stream()
                .map(FinanceiroMapper::toAcaoPosRelatorioDto)
                .toList();
    }
}

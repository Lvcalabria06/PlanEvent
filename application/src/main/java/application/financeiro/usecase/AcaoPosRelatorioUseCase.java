package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;

import java.util.List;

public interface AcaoPosRelatorioUseCase {

    FinanceiroDtos.AcaoPosRelatorioDto registrar(String relatorioId,
                                                  FinanceiroDtos.RegistrarAcaoPosRelatorioRequest request);

    FinanceiroDtos.AcaoPosRelatorioDto marcarComoTratada(String acaoId);

    List<FinanceiroDtos.AcaoPosRelatorioDto> listarPorRelatorio(String relatorioId);
}

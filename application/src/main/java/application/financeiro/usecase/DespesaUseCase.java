package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;

import java.util.List;

public interface DespesaUseCase {

    List<FinanceiroDtos.DespesaDto> listar(String eventoId, String categoria, String fornecedorId);

    List<FinanceiroDtos.DespesaDto> listarPendentes(String eventoId);

    List<FinanceiroDtos.DesvioDto> desvios(String eventoId);

    FinanceiroDtos.DespesaDto buscar(String despesaId);

    FinanceiroDtos.DespesaDto registrar(String eventoId, FinanceiroDtos.RegistrarDespesaRequest request, String usuarioId);

    FinanceiroDtos.DespesaDto atualizar(String despesaId, FinanceiroDtos.AtualizarDespesaRequest request);

    void excluir(String despesaId);

    FinanceiroDtos.DespesaDto aprovar(String despesaId, FinanceiroDtos.AprovarDespesaRequest request);

    FinanceiroDtos.DespesaDto rejeitar(String despesaId, FinanceiroDtos.RejeitarDespesaRequest request);
}

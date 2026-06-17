package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.financeiro.entity.Despesa;
import domain.financeiro.service.DespesaService;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.util.List;

public class DespesaUseCaseImpl implements DespesaUseCase {

    private final DespesaService despesaService;

    public DespesaUseCaseImpl(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    @Override
    public List<FinanceiroDtos.DespesaDto> listar(String eventoId, String categoria, String fornecedorId) {
        if (categoria != null && !categoria.isBlank()) {
            return despesaService
                    .pesquisarPorCategoria(eventoId, CategoriaDespesa.valueOf(categoria))
                    .stream()
                    .map(FinanceiroMapper::toDespesaDto)
                    .toList();
        }
        if (fornecedorId != null && !fornecedorId.isBlank()) {
            return despesaService
                    .pesquisarPorFornecedor(eventoId, fornecedorId)
                    .stream()
                    .map(FinanceiroMapper::toDespesaDto)
                    .toList();
        }
        return despesaService.listarDespesasPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toDespesaDto)
                .toList();
    }

    @Override
    public List<FinanceiroDtos.DespesaDto> listarPendentes(String eventoId) {
        return despesaService.listarDespesasPorEvento(eventoId).stream()
                .filter(d -> d.getStatus() == domain.financeiro.valueobject.StatusDespesa.PENDENTE_APROVACAO)
                .map(FinanceiroMapper::toDespesaDto)
                .toList();
    }

    @Override
    public List<FinanceiroDtos.DesvioDto> desvios(String eventoId) {
        return despesaService.calcularDesviosPorEvento(eventoId).stream()
                .map(FinanceiroMapper::toDesvioDto)
                .toList();
    }

    @Override
    public FinanceiroDtos.DespesaDto buscar(String despesaId) {
        return FinanceiroMapper.toDespesaDto(despesaService.buscarDespesa(despesaId));
    }

    @Override
    public FinanceiroDtos.DespesaDto registrar(String eventoId, FinanceiroDtos.RegistrarDespesaRequest request, String usuarioId) {
        Despesa despesa = new Despesa(
                eventoId,
                CategoriaDespesa.valueOf(request.categoria()),
                request.fornecedorId(),
                request.valor(),
                request.data(),
                usuarioId);
        return FinanceiroMapper.toDespesaDto(despesaService.registrarDespesa(despesa));
    }

    @Override
    public FinanceiroDtos.DespesaDto atualizar(String despesaId, FinanceiroDtos.AtualizarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.atualizarDespesa(despesaId, request.valor(), request.data()));
    }

    @Override
    public void excluir(String despesaId) {
        despesaService.excluirDespesa(despesaId);
    }

    @Override
    public FinanceiroDtos.DespesaDto aprovar(String despesaId, FinanceiroDtos.AprovarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.aprovarDespesa(despesaId, request.aprovadorId()));
    }

    @Override
    public FinanceiroDtos.DespesaDto rejeitar(String despesaId, FinanceiroDtos.RejeitarDespesaRequest request) {
        return FinanceiroMapper.toDespesaDto(
                despesaService.rejeitarDespesa(despesaId, request.aprovadorId(), request.motivo()));
    }
}

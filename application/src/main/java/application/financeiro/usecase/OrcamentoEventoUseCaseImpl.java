package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;
import application.financeiro.mapper.FinanceiroMapper;
import domain.financeiro.service.OrcamentoEventoService;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.util.List;

public class OrcamentoEventoUseCaseImpl implements OrcamentoEventoUseCase {

    private final OrcamentoEventoService service;

    public OrcamentoEventoUseCaseImpl(OrcamentoEventoService service) {
        this.service = service;
    }

    @Override
    public FinanceiroDtos.OrcamentoEventoDto criar(String eventoId,
                                                    FinanceiroDtos.CriarOrcamentoRequest request) {
        return FinanceiroMapper.toOrcamentoDto(
                service.criarOrcamento(eventoId, request.valorTotal()));
    }

    @Override
    public FinanceiroDtos.OrcamentoEventoDto buscar(String eventoId) {
        return FinanceiroMapper.toOrcamentoDto(service.buscarPorEvento(eventoId));
    }

    @Override
    public FinanceiroDtos.CategoriaOrcamentoDto adicionarCategoria(
            String eventoId,
            FinanceiroDtos.AdicionarCategoriaOrcamentoRequest request) {
        return FinanceiroMapper.toCategoriaOrcamentoDto(
                service.adicionarCategoria(
                        eventoId,
                        CategoriaDespesa.valueOf(request.categoria()),
                        request.valorPrevisto()));
    }

    @Override
    public FinanceiroDtos.CategoriaOrcamentoDto atualizarCategoria(
            String eventoId,
            String categoria,
            FinanceiroDtos.AtualizarCategoriaOrcamentoRequest request) {
        return FinanceiroMapper.toCategoriaOrcamentoDto(
                service.atualizarCategoria(
                        eventoId,
                        CategoriaDespesa.valueOf(categoria),
                        request.valorPrevisto()));
    }

    @Override
    public List<FinanceiroDtos.CategoriaOrcamentoDto> listarCategorias(String eventoId) {
        return service.listarCategorias(eventoId).stream()
                .map(FinanceiroMapper::toCategoriaOrcamentoDto)
                .toList();
    }
}

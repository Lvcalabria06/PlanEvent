package application.financeiro.usecase;

import application.financeiro.dto.FinanceiroDtos;

import java.util.List;

public interface OrcamentoEventoUseCase {

    FinanceiroDtos.OrcamentoEventoDto criar(String eventoId, FinanceiroDtos.CriarOrcamentoRequest request);

    FinanceiroDtos.OrcamentoEventoDto buscar(String eventoId);

    FinanceiroDtos.CategoriaOrcamentoDto adicionarCategoria(String eventoId,
                                                             FinanceiroDtos.AdicionarCategoriaOrcamentoRequest request);

    FinanceiroDtos.CategoriaOrcamentoDto atualizarCategoria(String eventoId,
                                                             String categoria,
                                                             FinanceiroDtos.AtualizarCategoriaOrcamentoRequest request);

    List<FinanceiroDtos.CategoriaOrcamentoDto> listarCategorias(String eventoId);
}

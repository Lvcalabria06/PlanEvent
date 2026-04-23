package domain.financeiro.repository;

import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.util.List;
import java.util.Optional;

public interface CategoriaOrcamentoRepository {

    CategoriaOrcamento salvar(CategoriaOrcamento categoria);

    //RN6: Busca o orçamento previsto de uma categoria para um orçamento de evento.
    Optional<CategoriaOrcamento> buscarPorOrcamentoECategoria(String orcamentoId, CategoriaDespesa categoria);

    List<CategoriaOrcamento> listarPorOrcamentoId(String orcamentoId);
}

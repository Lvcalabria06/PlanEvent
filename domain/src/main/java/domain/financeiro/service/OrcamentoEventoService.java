package domain.financeiro.service;

import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.util.List;

public interface OrcamentoEventoService {

    OrcamentoEvento criarOrcamento(String eventoId, BigDecimal valorTotal);

    OrcamentoEvento buscarPorEvento(String eventoId);

    CategoriaOrcamento adicionarCategoria(String eventoId, CategoriaDespesa categoria, BigDecimal valorPrevisto);

    CategoriaOrcamento atualizarCategoria(String eventoId, CategoriaDespesa categoria, BigDecimal novoValor);

    List<CategoriaOrcamento> listarCategorias(String eventoId);
}

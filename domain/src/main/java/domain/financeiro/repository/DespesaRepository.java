package domain.financeiro.repository;

import domain.financeiro.entity.Despesa;
import domain.financeiro.valueobject.CategoriaDespesa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DespesaRepository {

    Despesa salvar(Despesa despesa);

    Optional<Despesa> buscarPorId(String id);

    void excluir(String id);

    List<Despesa> listarPorEventoId(String eventoId);

    List<Despesa> listarPorEventoECategoria(String eventoId, CategoriaDespesa categoria);

    List<Despesa> listarPorEventoEFornecedor(String eventoId, String fornecedorId);

    List<Despesa> listarPorFornecedorId(String fornecedorId);

    BigDecimal somarValoresPorEventoECategoria(String eventoId, CategoriaDespesa categoria);

    BigDecimal somarValoresAtivosPorEventoECategoria(String eventoId, CategoriaDespesa categoria);
}

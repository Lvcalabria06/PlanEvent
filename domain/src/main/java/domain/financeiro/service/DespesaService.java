package domain.financeiro.service;

import domain.financeiro.entity.Despesa;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.DesvioOrcamentario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DespesaService {

    Despesa registrarDespesa(Despesa despesa);

    Despesa buscarDespesa(String id);

    List<Despesa> listarDespesasPorEvento(String eventoId);

    List<Despesa> pesquisarPorCategoria(String eventoId, CategoriaDespesa categoria);

    List<Despesa> pesquisarPorFornecedor(String eventoId, String fornecedorId);

    Despesa atualizarDespesa(String despesaId, BigDecimal novoValor, LocalDateTime novaData);

    void excluirDespesa(String despesaId);

    DesvioOrcamentario calcularDesvio(String eventoId, CategoriaDespesa categoria);

    List<DesvioOrcamentario> calcularDesviosPorEvento(String eventoId);

    Despesa aprovarDespesa(String despesaId, String aprovadorId);

    Despesa rejeitarDespesa(String despesaId, String aprovadorId, String motivo);
}

package domain.financeiro.service;

import domain.financeiro.entity.Despesa;
import domain.financeiro.valueobject.DesvioOrcamentario;

import java.util.List;

public interface DespesaService {

    Despesa registrarDespesa(Despesa despesa);

    Despesa buscarDespesa(String id);

    List<Despesa> listarDespesasPorEvento(String eventoId);

    DesvioOrcamentario calcularDesvio(String eventoId, domain.financeiro.valueobject.CategoriaDespesa categoria);

    List<DesvioOrcamentario> calcularDesviosPorEvento(String eventoId);
}

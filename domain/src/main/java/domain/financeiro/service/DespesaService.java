package domain.financeiro.service;

import domain.financeiro.entity.Despesa;
import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.DesvioOrcamentario;

import java.util.List;

public interface DespesaService {

    Despesa registrarDespesa(Despesa despesa);

    Despesa buscarDespesa(String id);

    List<Despesa> listarDespesasPorEvento(String eventoId);

    DesvioOrcamentario calcularDesvio(String eventoId, CategoriaDespesa categoria);

    List<DesvioOrcamentario> calcularDesviosPorEvento(String eventoId);

    Despesa aprovarDespesa(String despesaId, String aprovadorId);

    Despesa rejeitarDespesa(String despesaId, String aprovadorId, String motivo);
}

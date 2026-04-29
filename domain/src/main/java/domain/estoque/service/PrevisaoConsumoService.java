package domain.estoque.service;

import domain.estoque.entity.PrevisaoConsumo;

import java.util.Map;

public interface PrevisaoConsumoService {
    PrevisaoConsumo gerarPrevisao(String eventoId, String usuarioId);
    PrevisaoConsumo ajustarPrevisao(String previsaoId, Map<String, Integer> quantidadesAjustadas, String usuarioId);
    PrevisaoConsumo ajustarPrevisao(String previsaoId, Map<String, Integer> quantidadesAjustadas, String usuarioId, String justificativa);
    PrevisaoConsumo recalcularPrevisao(String previsaoId, String usuarioId);
    PrevisaoConsumo invalidarPrevisaoPorAlteracaoEvento(String eventoId, String usuarioId);
    PrevisaoConsumo buscarPorEvento(String eventoId);
}

package domain.estoque.repository;

import domain.estoque.entity.PrevisaoConsumo;

import java.util.Optional;

public interface PrevisaoConsumoRepository {
    PrevisaoConsumo salvar(PrevisaoConsumo previsaoConsumo);
    Optional<PrevisaoConsumo> buscarPorId(String id);
    Optional<PrevisaoConsumo> buscarPorEventoId(String eventoId);
}

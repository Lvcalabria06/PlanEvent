package domain.financeiro.repository;

import domain.financeiro.entity.OrcamentoEvento;

import java.util.Optional;

public interface OrcamentoEventoRepository {

    OrcamentoEvento salvar(OrcamentoEvento orcamento);

    Optional<OrcamentoEvento> buscarPorId(String id);

    //RN6
    Optional<OrcamentoEvento> buscarPorEventoId(String eventoId);
}

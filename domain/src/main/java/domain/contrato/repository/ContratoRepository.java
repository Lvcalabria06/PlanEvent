package domain.contrato.repository;

import domain.contrato.entity.Contrato;

import java.util.List;
import java.util.Optional;

public interface ContratoRepository {

    Contrato salvar(Contrato contrato);

    Optional<Contrato> buscarPorId(String id);

    List<Contrato> listarPorEventoId(String eventoId);
}

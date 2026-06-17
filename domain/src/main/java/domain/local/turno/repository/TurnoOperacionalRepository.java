package domain.local.turno.repository;

import domain.local.turno.entity.TurnoOperacional;

import java.util.List;
import java.util.Optional;

public interface TurnoOperacionalRepository {
    TurnoOperacional salvar(TurnoOperacional turno);
    Optional<TurnoOperacional> buscarPorId(String id);
    List<TurnoOperacional> buscarPorLocalId(String localId);
    void remover(String id);
}

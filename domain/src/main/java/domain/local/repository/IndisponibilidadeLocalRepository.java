package domain.local.repository;

import domain.local.entity.IndisponibilidadeLocal;

import java.util.List;
import java.util.Optional;

public interface IndisponibilidadeLocalRepository {
    IndisponibilidadeLocal salvar(IndisponibilidadeLocal indisponibilidade);

    Optional<IndisponibilidadeLocal> buscarPorId(String id);

    List<IndisponibilidadeLocal> listarPorLocalId(String localId);
}

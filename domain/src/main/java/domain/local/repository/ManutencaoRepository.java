package domain.local.repository;

import domain.local.entity.ManutencaoLocal;
import java.util.List;
import java.util.Optional;

public interface ManutencaoRepository {
    ManutencaoLocal salvar(ManutencaoLocal manutencao);
    Optional<ManutencaoLocal> buscarPorId(String id);
    List<ManutencaoLocal> buscarPorLocalId(String localId);
    void remover(String id);
}

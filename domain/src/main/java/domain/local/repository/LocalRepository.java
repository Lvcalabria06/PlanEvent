package domain.local.repository;

import domain.local.entity.Local;
import java.util.List;
import java.util.Optional;

public interface LocalRepository {
    Local salvar(Local local);
    Optional<Local> buscarPorId(String id);
    List<Local> listarTodos();
}

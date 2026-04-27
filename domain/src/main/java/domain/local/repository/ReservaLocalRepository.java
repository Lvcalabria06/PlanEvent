package domain.local.repository;

import domain.local.entity.ReservaLocal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaLocalRepository {
    ReservaLocal salvar(ReservaLocal reserva);
    Optional<ReservaLocal> buscarPorId(String id);
    
    // Metodo para buscar reservas que sobrepoem um determinado periodo de tempo em um local especifico.
    // Isso é essencial para as validações de Manutenção e Indisponibilidade.
    List<ReservaLocal> buscarReservasPorLocalEPeriodo(String localId, LocalDateTime inicio, LocalDateTime fim);
}

package domain.local.service;

import domain.local.entity.ReservaLocal;

import java.time.LocalDateTime;

public interface ReservaLocalService {
    ReservaLocal reservar(String localId, String eventoId, LocalDateTime inicio, LocalDateTime fim);
}

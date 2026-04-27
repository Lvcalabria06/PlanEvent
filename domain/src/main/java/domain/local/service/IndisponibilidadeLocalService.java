package domain.local.service;

import domain.local.entity.IndisponibilidadeLocal;

import java.time.LocalDateTime;

public interface IndisponibilidadeLocalService {
    IndisponibilidadeLocal registrarIndisponibilidade(String localId, LocalDateTime inicio, LocalDateTime fim, String motivo);
}

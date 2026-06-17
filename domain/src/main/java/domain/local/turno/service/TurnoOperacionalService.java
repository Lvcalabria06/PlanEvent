package domain.local.turno.service;

import domain.local.turno.entity.TurnoOperacional;

import java.time.LocalTime;
import java.util.List;

public interface TurnoOperacionalService {
    TurnoOperacional cadastrarTurno(String localId, String nome, LocalTime horaInicio, LocalTime horaFim,
                                    String diasDaSemana, Integer capacidade, String observacoes);
    TurnoOperacional editarTurno(String turnoId, String nome, LocalTime horaInicio, LocalTime horaFim,
                                 String diasDaSemana, Integer capacidade, String observacoes);
    void desativarTurno(String turnoId);
    List<TurnoOperacional> listarTurnosPorLocal(String localId);
}

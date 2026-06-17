package domain.local.turno.service;

import domain.local.repository.LocalRepository;
import domain.local.turno.entity.TurnoOperacional;
import domain.local.turno.repository.TurnoOperacionalRepository;

import java.time.LocalTime;
import java.util.List;

public class TurnoOperacionalServiceImpl implements TurnoOperacionalService {

    private final TurnoOperacionalRepository turnoRepository;
    private final LocalRepository localRepository;

    public TurnoOperacionalServiceImpl(TurnoOperacionalRepository turnoRepository, LocalRepository localRepository) {
        this.turnoRepository = turnoRepository;
        this.localRepository = localRepository;
    }

    @Override
    public TurnoOperacional cadastrarTurno(String localId, String nome, LocalTime horaInicio, LocalTime horaFim,
                                           String diasDaSemana, Integer capacidade, String observacoes) {
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        TurnoOperacional turno = new TurnoOperacional(localId, nome, horaInicio, horaFim, diasDaSemana, capacidade, observacoes);
        return turnoRepository.salvar(turno);
    }

    @Override
    public TurnoOperacional editarTurno(String turnoId, String nome, LocalTime horaInicio, LocalTime horaFim,
                                        String diasDaSemana, Integer capacidade, String observacoes) {
        TurnoOperacional turno = turnoRepository.buscarPorId(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno não encontrado."));
        turno.atualizar(nome, horaInicio, horaFim, diasDaSemana, capacidade, observacoes);
        return turnoRepository.salvar(turno);
    }

    @Override
    public void desativarTurno(String turnoId) {
        TurnoOperacional turno = turnoRepository.buscarPorId(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno não encontrado."));
        turno.desativar();
        turnoRepository.salvar(turno);
    }

    @Override
    public List<TurnoOperacional> listarTurnosPorLocal(String localId) {
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        return turnoRepository.buscarPorLocalId(localId);
    }
}

package application.evento.dto;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

import java.time.LocalDateTime;

public record CriarEventoRequest(
        String nome,
        TipoEvento tipo,
        PorteEvento porte,
        int quantidadeEstimadaParticipantes,
        String objetivo,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String requisitosInfraestrutura
) {}

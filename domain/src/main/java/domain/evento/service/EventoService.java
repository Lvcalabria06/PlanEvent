package domain.evento.service;

import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EventoService {
    Evento cadastrarEvento(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo);

    Evento cadastrarEvento(
            String nome,
            TipoEvento tipo,
            PorteEvento porte,
            int quantidadeEstimadaParticipantes,
            String objetivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String requisitosInfraestrutura);

    Evento editarEvento(String eventoId, String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo);

    Evento editarEvento(
            String eventoId,
            String nome,
            TipoEvento tipo,
            PorteEvento porte,
            int quantidadeEstimadaParticipantes,
            String objetivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String requisitosInfraestrutura);

    List<Evento> listarEventos();

    Evento visualizarEvento(String eventoId);

    Evento confirmarPreparacaoInicial(String eventoId);

    List<Local> listarLocaisCompativeis(String eventoId, BigDecimal tetoCusto);

    Evento vincularLocalAoEvento(String eventoId, String localId, BigDecimal tetoCusto);

    Evento removerLocalDoEvento(String eventoId);

    Evento cancelarEvento(String eventoId);

    Evento concluirEvento(String eventoId);
}

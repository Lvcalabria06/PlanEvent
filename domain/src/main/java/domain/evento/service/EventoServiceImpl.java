package domain.evento.service;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.Local;
import domain.local.repository.LocalRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final LocalRepository localRepository;

    public EventoServiceImpl(EventoRepository eventoRepository, LocalRepository localRepository) {
        this.eventoRepository = eventoRepository;
        this.localRepository = localRepository;
    }

    @Override
    public Evento cadastrarEvento(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        Evento evento = new Evento(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo, null);
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento editarEvento(String eventoId, String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        Evento evento = buscarEventoExistente(eventoId);
        evento.atualizarDados(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo);
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento visualizarEvento(String eventoId) {
        return buscarEventoExistente(eventoId);
    }

    @Override
    public Evento confirmarPreparacaoInicial(String eventoId) {
        Evento evento = buscarEventoExistente(eventoId);
        evento.confirmarPlanejamento();
        return eventoRepository.salvar(evento);
    }

    @Override
    public List<Local> listarLocaisCompativeis(String eventoId, BigDecimal tetoCusto) {
        Evento evento = buscarEventoExistente(eventoId);
        BigDecimal tetoValidado = validarTeto(tetoCusto);

        return localRepository.listarTodos()
                .stream()
                .filter(Local::isAtivo)
                .filter(local -> local.getCapacidade() >= evento.getQuantidadeEstimadaParticipantes())
                .filter(local -> local.getCusto().compareTo(tetoValidado) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public Evento vincularLocalAoEvento(String eventoId, String localId, BigDecimal tetoCusto) {
        BigDecimal tetoValidado = validarTeto(tetoCusto);
        Evento evento = buscarEventoExistente(eventoId);
        Local local = buscarLocalExistente(localId);

        validarLocalCompativel(evento, local, tetoValidado);
        evento.alterarLocal(local.getId());
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento removerLocalDoEvento(String eventoId) {
        Evento evento = buscarEventoExistente(eventoId);
        evento.alterarLocal(null);
        return eventoRepository.salvar(evento);
    }

    private Evento buscarEventoExistente(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }

    private Local buscarLocalExistente(String localId) {
        if (localId == null || localId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do local é obrigatório.");
        }
        return localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
    }

    private BigDecimal validarTeto(BigDecimal tetoCusto) {
        if (tetoCusto == null || tetoCusto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Teto de custo deve ser maior ou igual a zero.");
        }
        return tetoCusto;
    }

    private void validarLocalCompativel(Evento evento, Local local, BigDecimal tetoCusto) {
        if (!local.isAtivo()) {
            throw new IllegalArgumentException("Local inativo não pode ser vinculado ao evento.");
        }
        if (local.getCapacidade() < evento.getQuantidadeEstimadaParticipantes()) {
            throw new IllegalArgumentException("Capacidade do local é inferior à quantidade estimada de participantes.");
        }
        if (local.getCusto().compareTo(tetoCusto) > 0) {
            throw new IllegalArgumentException("Custo do local excede o teto informado.");
        }
    }
}

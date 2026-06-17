package domain.evento.service;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.contrato.entity.Contrato;
import domain.contrato.repository.ContratoRepository;
import domain.contrato.valueobject.StatusContrato;
import domain.local.entity.Local;
import domain.local.repository.LocalRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;
    private final LocalRepository localRepository;
    private final ContratoRepository contratoRepository;

    public EventoServiceImpl(
            EventoRepository eventoRepository,
            LocalRepository localRepository,
            ContratoRepository contratoRepository) {
        this.eventoRepository = eventoRepository;
        this.localRepository = localRepository;
        this.contratoRepository = contratoRepository;
    }

    @Override
    public Evento cadastrarEvento(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        return cadastrarEvento(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo, null, null, null);
    }

    @Override
    public Evento cadastrarEvento(
            String nome,
            TipoEvento tipo,
            PorteEvento porte,
            int quantidadeEstimadaParticipantes,
            String objetivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String requisitosInfraestrutura) {
        Evento evento = new Evento(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo, null);
        aplicarPeriodoSeInformado(evento, dataInicio, dataFim);
        aplicarRequisitosSeInformado(evento, requisitosInfraestrutura);
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento editarEvento(String eventoId, String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        return editarEvento(eventoId, nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo, null, null, null);
    }

    @Override
    public Evento editarEvento(
            String eventoId,
            String nome,
            TipoEvento tipo,
            PorteEvento porte,
            int quantidadeEstimadaParticipantes,
            String objetivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String requisitosInfraestrutura) {
        Evento evento = buscarEventoExistente(eventoId);
        evento.atualizarDados(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo);
        aplicarPeriodoSeInformado(evento, dataInicio, dataFim);
        aplicarRequisitosSeInformado(evento, requisitosInfraestrutura);
        return eventoRepository.salvar(evento);
    }

    @Override
    public List<Evento> listarEventos() {
        return eventoRepository.listarTodos().stream()
                .filter(evento -> !evento.isCancelado())
                .toList();
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

    @Override
    public Evento concluirEvento(String eventoId) {
        Evento evento = buscarEventoExistente(eventoId);
        evento.concluirEvento();
        return eventoRepository.salvar(evento);
    }

    @Override
    public Evento cancelarEvento(String eventoId) {
        Evento evento = buscarEventoExistente(eventoId);
        validarSemContratosAtivos(eventoId);
        evento.cancelar();
        return eventoRepository.salvar(evento);
    }

    private void aplicarPeriodoSeInformado(Evento evento, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio != null && dataFim != null) {
            evento.definirJanelaPlanejamento(dataInicio, dataFim);
        } else if (dataInicio != null || dataFim != null) {
            throw new IllegalArgumentException("Informe data de início e término ou omita ambas.");
        }
    }

    private void aplicarRequisitosSeInformado(Evento evento, String requisitosInfraestrutura) {
        if (requisitosInfraestrutura != null) {
            evento.definirRequisitosInfraestrutura(requisitosInfraestrutura);
        }
    }

    private Evento buscarEventoExistente(String eventoId) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
        if (evento.isCancelado()) {
            throw new IllegalArgumentException("Evento cancelado.");
        }
        return evento;
    }

    private void validarSemContratosAtivos(String eventoId) {
        for (Contrato contrato : contratoRepository.listarPorEventoId(eventoId)) {
            StatusContrato status = contrato.getStatus();
            if (status != StatusContrato.ENCERRADO && status != StatusContrato.CANCELADO) {
                throw new IllegalStateException(
                        "Não é possível cancelar evento com contratos ativos vinculados.");
            }
        }
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

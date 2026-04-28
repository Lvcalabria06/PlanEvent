package domain.local.service;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.local.entity.LayoutLocal;
import domain.local.entity.Local;
import domain.local.repository.LocalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LayoutLocalServiceImpl implements LayoutLocalService {

    private final LocalRepository localRepository;
    private final EventoRepository eventoRepository;
    private final ConcurrentHashMap<String, Set<String>> eventosPorLayout = new ConcurrentHashMap<>();

    public LayoutLocalServiceImpl(LocalRepository localRepository, EventoRepository eventoRepository) {
        this.localRepository = localRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public LayoutLocal cadastrarLayout(String localId, String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel) {
        Local local = buscarLocalAtivo(localId);
        LayoutLocal layout = local.adicionarLayout(nome, descricao, capacidadeMaxima, usuarioResponsavel);
        localRepository.salvar(local);
        return layout;
    }

    @Override
    public LayoutLocal atualizarLayout(
            String localId,
            String layoutId,
            String nome,
            String descricao,
            int capacidadeMaxima,
            String usuarioResponsavel) {
        Local local = buscarLocalAtivo(localId);
        LayoutLocal antes = local.buscarLayoutPorId(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Layout não encontrado para o local."));
        int capacidadeAnterior = antes.getCapacidadeMaxima();
        LayoutLocal atualizado = local.atualizarLayout(layoutId, nome, descricao, capacidadeMaxima, usuarioResponsavel);
        localRepository.salvar(local);
        if (capacidadeAnterior != capacidadeMaxima) {
            marcarRevalidacaoEventos(layoutId);
        }
        return atualizado;
    }

    @Override
    public List<LayoutLocal> listarLayouts(String localId) {
        return buscarLocalAtivo(localId).listarLayouts();
    }

    @Override
    public List<CompatibilidadeLayoutEvento> analisarCompatibilidadeParaEvento(String eventoId, String localId) {
        Evento evento = buscarEvento(eventoId);
        Local local = buscarLocalAtivo(localId);
        List<CompatibilidadeLayoutEvento> saida = new ArrayList<>();
        for (LayoutLocal layout : local.listarLayouts()) {
            boolean compativel = evento.getQuantidadeEstimadaParticipantes() <= layout.getCapacidadeMaxima();
            String justificativa = compativel
                    ? "Layout compatível para a quantidade estimada de participantes."
                    : "Layout incompatível: capacidade inferior ao estimado do evento.";
            saida.add(new CompatibilidadeLayoutEvento(
                    layout.getId(),
                    layout.getNome(),
                    layout.getCapacidadeMaxima(),
                    compativel,
                    justificativa));
        }
        return saida;
    }

    @Override
    public void associarLayoutAoEvento(
            String eventoId,
            String localId,
            String layoutId,
            String justificativaExcecao,
            String usuarioResponsavel) {
        if (usuarioResponsavel == null || usuarioResponsavel.isBlank()) {
            throw new IllegalArgumentException("Usuário responsável é obrigatório.");
        }
        Evento evento = buscarEvento(eventoId);
        Local local = buscarLocalAtivo(localId);
        if (evento.getLocalId() == null || !evento.getLocalId().equals(local.getId())) {
            throw new IllegalArgumentException("Evento deve estar vinculado ao local antes de selecionar layout.");
        }
        LayoutLocal layout = local.buscarLayoutPorId(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Layout não encontrado para o local."));
        boolean compativel = evento.getQuantidadeEstimadaParticipantes() <= layout.getCapacidadeMaxima();
        if (!compativel && (justificativaExcecao == null || justificativaExcecao.isBlank())) {
            throw new IllegalArgumentException("Layout incompatível exige justificativa de exceção.");
        }
        evento.associarLayout(layout.getId(), justificativaExcecao);
        eventoRepository.salvar(evento);
        eventosPorLayout.computeIfAbsent(layoutId, key -> ConcurrentHashMap.newKeySet()).add(evento.getId());
    }

    private void marcarRevalidacaoEventos(String layoutId) {
        Set<String> ids = eventosPorLayout.get(layoutId);
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (String eventoId : ids) {
            eventoRepository.buscarPorId(eventoId).ifPresent(evento -> {
                if (!evento.isConcluido()) {
                    evento.marcarValidacaoLayoutPendente();
                    eventoRepository.salvar(evento);
                }
            });
        }
    }

    private Local buscarLocalAtivo(String localId) {
        Local local = localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        if (!local.isAtivo()) {
            throw new IllegalArgumentException("Layout só pode ser gerenciado para local ativo.");
        }
        return local;
    }

    private Evento buscarEvento(String eventoId) {
        return eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }
}

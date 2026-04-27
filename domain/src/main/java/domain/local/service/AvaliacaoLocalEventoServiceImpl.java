package domain.local.service;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.local.entity.AvaliacaoLocalEvento;
import domain.local.repository.AvaliacaoLocalEventoRepository;
import domain.local.valueobject.NivelAdequacao;

import java.util.List;
import java.util.stream.Collectors;

public class AvaliacaoLocalEventoServiceImpl implements AvaliacaoLocalEventoService {

    private final EventoRepository eventoRepository;
    private final AvaliacaoLocalEventoRepository avaliacaoLocalEventoRepository;

    public AvaliacaoLocalEventoServiceImpl(
            EventoRepository eventoRepository,
            AvaliacaoLocalEventoRepository avaliacaoLocalEventoRepository) {
        this.eventoRepository = eventoRepository;
        this.avaliacaoLocalEventoRepository = avaliacaoLocalEventoRepository;
    }

    @Override
    public AvaliacaoLocalEvento registrarAvaliacao(
            String eventoId,
            String localId,
            NivelAdequacao nivel,
            String justificativa,
            String gestorId) {
        if (nivel == null) {
            throw new IllegalArgumentException("Nível de adequação é obrigatório.");
        }

        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (evento.getLocalId() == null || !evento.getLocalId().equals(localId)) {
            throw new IllegalStateException("O local informado não está vinculado a este evento.");
        }
        if (!evento.isConcluido()) {
            throw new IllegalStateException("A avaliação do local só pode ser registrada após o evento concluir (uso do local finalizado).");
        }

        if (avaliacaoLocalEventoRepository.buscarPorEventoIdELocalId(eventoId, localId).isPresent()) {
            throw new IllegalStateException("Já existe avaliação deste local para o evento.");
        }

        AvaliacaoLocalEvento avaliacao = new AvaliacaoLocalEvento(eventoId, localId, nivel, justificativa, gestorId);
        return avaliacaoLocalEventoRepository.salvar(avaliacao);
    }

    @Override
    public List<AvaliacaoLocalEvento> listarAvaliacoesDoLocal(String localId) {
        if (localId == null || localId.isBlank()) {
            throw new IllegalArgumentException("O local informado é obrigatório.");
        }
        return avaliacaoLocalEventoRepository.listarPorLocalId(localId)
                .stream()
                .sorted((a, b) -> b.getDataAvaliacao().compareTo(a.getDataAvaliacao()))
                .collect(Collectors.toList());
    }
}

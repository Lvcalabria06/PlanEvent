package domain.local.service;

import domain.evento.entity.Evento;
import domain.evento.repository.EventoRepository;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;
import domain.local.entity.AvaliacaoContextualLocal;
import domain.local.repository.AvaliacaoContextualLocalRepository;
import domain.local.repository.LocalRepository;

import java.util.List;
import java.util.Map;

public class AvaliacaoContextualLocalServiceImpl implements AvaliacaoContextualLocalService {

    private final EventoRepository eventoRepository;
    private final LocalRepository localRepository;
    private final AvaliacaoContextualLocalRepository avaliacaoRepository;

    public AvaliacaoContextualLocalServiceImpl(EventoRepository eventoRepository,
            LocalRepository localRepository,
            AvaliacaoContextualLocalRepository avaliacaoRepository) {
        this.eventoRepository = eventoRepository;
        this.localRepository = localRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public AvaliacaoContextualLocal registrarAvaliacao(
            String eventoId,
            String localId,
            Map<String, Integer> notasPorCriterio,
            String justificativa,
            String usuarioResponsavel) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        if (!evento.isConcluido()) {
            throw new IllegalStateException("Avaliacao so pode ser registrada apos conclusao do evento.");
        }
        if (evento.getLocalId() == null || !evento.getLocalId().equals(localId)) {
            throw new IllegalArgumentException("Local avaliado deve ser o local vinculado ao evento.");
        }
        if (avaliacaoRepository.existePorEventoIdELocalId(eventoId, localId)) {
            throw new IllegalStateException("Já existe avaliação principal para este evento e local.");
        }
        AvaliacaoContextualLocal avaliacao = new AvaliacaoContextualLocal(
                eventoId,
                localId,
                evento.getTipo(),
                evento.getPorte(),
                evento.getQuantidadeEstimadaParticipantes(),
                notasPorCriterio,
                justificativa,
                usuarioResponsavel);
        return avaliacaoRepository.salvar(avaliacao);
    }

    @Override
    public ResumoDesempenhoContextualLocal consultarResumo(String localId, TipoEvento tipoEvento, PorteEvento porteEvento) {
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        List<AvaliacaoContextualLocal> doLocal = avaliacaoRepository.buscarPorLocalId(localId);
        List<AvaliacaoContextualLocal> doContexto = doLocal.stream()
                .filter(a -> a.getTipoEvento() == tipoEvento && a.getPorteEvento() == porteEvento)
                .toList();

        double mediaGeral = media(doLocal);
        double mediaContexto = media(doContexto);
        boolean baixaBase = doContexto.size() < 3;

        return new ResumoDesempenhoContextualLocal(
                mediaGeral,
                mediaContexto,
                doLocal.size(),
                doContexto.size(),
                baixaBase,
                classificar(mediaGeral),
                classificar(mediaContexto));
    }

    @Override
    public List<AvaliacaoContextualLocal> listarHistorico(String localId) {
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));
        return avaliacaoRepository.buscarPorLocalId(localId);
    }

    private double media(List<AvaliacaoContextualLocal> lista) {
        if (lista.isEmpty()) return 0.0;
        double soma = 0.0;
        for (AvaliacaoContextualLocal avaliacao : lista) {
            soma += avaliacao.getNotaFinal();
        }
        return soma / lista.size();
    }

    private String classificar(double media) {
        if (media >= 4.0) return "RECOMENDADO";
        if (media >= 3.0) return "RECOMENDADO_COM_RESSALVAS";
        return "NAO_RECOMENDADO";
    }
}

package domain.estoque.entity;

import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.estoque.valueobject.TipoRegistroPrevisao;
import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PrevisaoConsumo {
    private final String id;
    private final String eventoId;
    private final String geradoPorUsuarioId;
    private final LocalDateTime dataGeracao;
    private StatusHistoricoPrevisao statusHistorico;
    private boolean fallbackUtilizado;
    private boolean invalidada;
    private int versaoAtual;
    private int totalEventosBase;
    private TipoEvento tipoEventoReferencia;
    private PorteEvento porteEventoReferencia;
    private long duracaoHorasReferencia;
    private List<ItemPrevisao> itens;
    private final List<RegistroHistoricoPrevisao> historicoRegistros;

    public PrevisaoConsumo(Evento evento,
                           String geradoPorUsuarioId,
                           StatusHistoricoPrevisao statusHistorico,
                           boolean fallbackUtilizado,
                           int totalEventosBase,
                           List<ItemPrevisao> itensPrevistos) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento e obrigatorio.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = evento.getId();
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = LocalDateTime.now();
        this.statusHistorico = statusHistorico;
        this.fallbackUtilizado = fallbackUtilizado;
        this.invalidada = false;
        this.versaoAtual = 1;
        this.totalEventosBase = totalEventosBase;
        this.itens = clonarItens(itensPrevistos);
        this.historicoRegistros = new ArrayList<>();
        atualizarReferenciaEvento(evento);
        registrarHistorico(TipoRegistroPrevisao.GERACAO_INICIAL, geradoPorUsuarioId, "Previsao inicial gerada.");
    }

    private PrevisaoConsumo(String id, String eventoId, String geradoPorUsuarioId, LocalDateTime dataGeracao,
                              StatusHistoricoPrevisao statusHistorico, boolean fallbackUtilizado, boolean invalidada,
                              int versaoAtual, int totalEventosBase, TipoEvento tipoEventoReferencia,
                              PorteEvento porteEventoReferencia, long duracaoHorasReferencia,
                              List<ItemPrevisao> itens, List<RegistroHistoricoPrevisao> historicoRegistros) {
        this.id = id;
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = dataGeracao;
        this.statusHistorico = statusHistorico;
        this.fallbackUtilizado = fallbackUtilizado;
        this.invalidada = invalidada;
        this.versaoAtual = versaoAtual;
        this.totalEventosBase = totalEventosBase;
        this.tipoEventoReferencia = tipoEventoReferencia;
        this.porteEventoReferencia = porteEventoReferencia;
        this.duracaoHorasReferencia = duracaoHorasReferencia;
        this.itens = new ArrayList<>(itens);
        this.historicoRegistros = new ArrayList<>(historicoRegistros);
    }

    public static PrevisaoConsumo reconstituir(String id, String eventoId, String geradoPorUsuarioId,
                                               LocalDateTime dataGeracao, StatusHistoricoPrevisao statusHistorico,
                                               boolean fallbackUtilizado, boolean invalidada, int versaoAtual,
                                               int totalEventosBase, TipoEvento tipoEventoReferencia,
                                               PorteEvento porteEventoReferencia, long duracaoHorasReferencia,
                                               List<ItemPrevisao> itens, List<RegistroHistoricoPrevisao> historicoRegistros) {
        return new PrevisaoConsumo(id, eventoId, geradoPorUsuarioId, dataGeracao, statusHistorico, fallbackUtilizado,
                invalidada, versaoAtual, totalEventosBase, tipoEventoReferencia, porteEventoReferencia,
                duracaoHorasReferencia, itens, historicoRegistros);
    }

    public void invalidarPorAlteracaoEvento(Evento evento, String usuarioId) {
        if (!possuiMudancaRelevante(evento)) {
            return;
        }
        this.invalidada = true;
        this.statusHistorico = StatusHistoricoPrevisao.INVALIDADA;
        registrarHistorico(TipoRegistroPrevisao.INVALIDACAO, usuarioId, "Previsao invalidada por alteracao relevante do evento.");
    }

    public void recalcular(Evento evento,
                           String usuarioId,
                           StatusHistoricoPrevisao novoStatus,
                           boolean novoFallback,
                           int novoTotalEventosBase,
                           List<ItemPrevisao> novosItens) {
        this.versaoAtual += 1;
        this.invalidada = false;
        this.statusHistorico = novoStatus;
        this.fallbackUtilizado = novoFallback;
        this.totalEventosBase = novoTotalEventosBase;
        this.itens = clonarItens(novosItens);
        atualizarReferenciaEvento(evento);
        registrarHistorico(TipoRegistroPrevisao.RECALCULO, usuarioId, "Previsao recalculada.");
    }

    public void ajustarQuantidades(Map<String, Integer> quantidadesAjustadas, String usuarioId, String justificativa) {
        for (ItemPrevisao item : itens) {
            Integer quantidade = quantidadesAjustadas.get(item.getItemEstoqueId());
            if (quantidade != null) {
                item.sobrescreverQuantidadeFinal(quantidade);
            }
        }
        registrarHistorico(TipoRegistroPrevisao.AJUSTE_MANUAL, usuarioId, justificativa);
    }

    public boolean possuiMudancaRelevante(Evento evento) {
        if (evento == null) {
            return false;
        }
        return evento.getTipo() != tipoEventoReferencia
                || evento.getPorte() != porteEventoReferencia
                || calcularDuracaoHoras(evento) != duracaoHorasReferencia;
    }

    private void atualizarReferenciaEvento(Evento evento) {
        this.tipoEventoReferencia = evento.getTipo();
        this.porteEventoReferencia = evento.getPorte();
        this.duracaoHorasReferencia = calcularDuracaoHoras(evento);
    }

    private long calcularDuracaoHoras(Evento evento) {
        if (evento.getJanelaInicioPlanejamento() != null && evento.getJanelaFimPlanejamento() != null) {
            long horas = Duration.between(evento.getJanelaInicioPlanejamento(), evento.getJanelaFimPlanejamento()).toHours();
            return Math.max(horas, 1);
        }
        return 1;
    }

    private List<ItemPrevisao> clonarItens(List<ItemPrevisao> origem) {
        return origem.stream()
                .map(item -> {
                    ItemPrevisao novo = new ItemPrevisao(
                            this.id,
                            item.getItemEstoqueId(),
                            item.getCategoriaConsumo(),
                            item.getQuantidadeEstimada(),
                            item.getQuantidadeMinima(),
                            item.getQuantidadeMaxima(),
                            item.getExplicacaoCalculo()
                    );
                    if (item.getQuantidadeFinal() != item.getQuantidadeEstimada()) {
                        novo.sobrescreverQuantidadeFinal(item.getQuantidadeFinal());
                    }
                    return novo;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void registrarHistorico(TipoRegistroPrevisao tipoRegistro, String usuarioId, String justificativa) {
        List<ItemPrevisaoHistorico> snapshot = itens.stream()
                .map(item -> new ItemPrevisaoHistorico(
                        item.getItemEstoqueId(),
                        item.getCategoriaConsumo(),
                        item.getQuantidadeEstimada(),
                        item.getQuantidadeFinal()))
                .collect(Collectors.toList());
        historicoRegistros.add(new RegistroHistoricoPrevisao(versaoAtual, tipoRegistro, usuarioId, justificativa, snapshot));
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public StatusHistoricoPrevisao getStatusHistorico() { return statusHistorico; }
    public boolean isFallbackUtilizado() { return fallbackUtilizado; }
    public boolean isInvalidada() { return invalidada; }
    public int getVersaoAtual() { return versaoAtual; }
    public int getTotalEventosBase() { return totalEventosBase; }
    public List<ItemPrevisao> getItens() { return Collections.unmodifiableList(itens); }
    public List<RegistroHistoricoPrevisao> getHistoricoRegistros() { return Collections.unmodifiableList(historicoRegistros); }
    public TipoEvento getTipoEventoReferencia() { return tipoEventoReferencia; }
    public PorteEvento getPorteEventoReferencia() { return porteEventoReferencia; }
    public long getDuracaoHorasReferencia() { return duracaoHorasReferencia; }
}

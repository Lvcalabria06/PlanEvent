package domain.estoque.entity;

import domain.estoque.valueobject.StatusHistoricoPrevisao;
import domain.estoque.valueobject.TipoRegistroPrevisao;
import domain.evento.entity.Evento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

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
    private int totalEventosBase;
    private TipoEvento tipoEventoReferencia;
    private PorteEvento porteEventoReferencia;
    private int quantidadeParticipantesReferencia;
    private String objetivoReferencia;
    private List<ItemPrevisao> itens;
    private final List<RegistroHistoricoPrevisao> historicoRegistros;

    public PrevisaoConsumo(Evento evento,
                           String geradoPorUsuarioId,
                           StatusHistoricoPrevisao statusHistorico,
                           int totalEventosBase,
                           List<ItemPrevisao> itensPrevistos) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento e obrigatorio.");
        }
        if (geradoPorUsuarioId == null || geradoPorUsuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuario responsavel e obrigatorio.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = evento.getId();
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = LocalDateTime.now();
        this.statusHistorico = statusHistorico;
        this.totalEventosBase = totalEventosBase;
        this.itens = itensPrevistos.stream()
                .map(item -> {
                    ItemPrevisao novoItem = new ItemPrevisao(this.id, item.getItemEstoqueId(), item.getQuantidadePrevista());
                    if (item.getQuantidadeAjustada() != item.getQuantidadePrevista()) {
                        novoItem.ajustarQuantidade(item.getQuantidadeAjustada());
                    }
                    return novoItem;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        this.historicoRegistros = new ArrayList<>();
        atualizarReferenciaEvento(evento);
        registrarHistorico(TipoRegistroPrevisao.GERACAO_INICIAL, geradoPorUsuarioId, "Previsao inicial gerada.");
    }

    public void ajustarQuantidades(Map<String, Integer> quantidadesAjustadas, String usuarioResponsavelId) {
        if (quantidadesAjustadas == null || quantidadesAjustadas.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um item para ajuste manual.");
        }
        for (ItemPrevisao item : itens) {
            Integer quantidadeAjustada = quantidadesAjustadas.get(item.getItemEstoqueId());
            if (quantidadeAjustada != null) {
                item.ajustarQuantidade(quantidadeAjustada);
            }
        }
        registrarHistorico(TipoRegistroPrevisao.AJUSTE_MANUAL, usuarioResponsavelId, "Ajuste manual realizado.");
    }

    public void recalcular(Evento evento,
                           String usuarioResponsavelId,
                           StatusHistoricoPrevisao novoStatusHistorico,
                           int novoTotalEventosBase,
                           List<ItemPrevisao> novosItens) {
        if (!possuiMudancaRelevante(evento)) {
            throw new IllegalStateException("Nao ha alteracoes relevantes no evento para recalculo da previsao.");
        }
        this.statusHistorico = novoStatusHistorico;
        this.totalEventosBase = novoTotalEventosBase;
        this.itens = novosItens.stream()
                .map(item -> {
                    ItemPrevisao novoItem = new ItemPrevisao(this.id, item.getItemEstoqueId(), item.getQuantidadePrevista());
                    if (item.getQuantidadeAjustada() != item.getQuantidadePrevista()) {
                        novoItem.ajustarQuantidade(item.getQuantidadeAjustada());
                    }
                    return novoItem;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        atualizarReferenciaEvento(evento);
        registrarHistorico(TipoRegistroPrevisao.RECALCULO, usuarioResponsavelId, "Previsao recalculada por mudanca relevante.");
    }

    public boolean possuiMudancaRelevante(Evento evento) {
        if (evento == null) {
            return false;
        }
        if (evento.getTipo() != tipoEventoReferencia || evento.getPorte() != porteEventoReferencia) {
            return true;
        }
        if (!normalizar(objetivoReferencia).equals(normalizar(evento.getObjetivo()))) {
            return true;
        }
        if (quantidadeParticipantesReferencia == 0) {
            return true;
        }
        int diferenca = Math.abs(evento.getQuantidadeEstimadaParticipantes() - quantidadeParticipantesReferencia);
        return diferenca >= Math.ceil(quantidadeParticipantesReferencia * 0.2);
    }

    private void atualizarReferenciaEvento(Evento evento) {
        this.tipoEventoReferencia = evento.getTipo();
        this.porteEventoReferencia = evento.getPorte();
        this.quantidadeParticipantesReferencia = evento.getQuantidadeEstimadaParticipantes();
        this.objetivoReferencia = evento.getObjetivo();
    }

    private void registrarHistorico(TipoRegistroPrevisao tipoRegistro, String usuarioResponsavelId, String observacao) {
        List<ItemPrevisaoHistorico> snapshot = itens.stream()
                .map(item -> new ItemPrevisaoHistorico(
                        item.getItemEstoqueId(),
                        item.getQuantidadePrevista(),
                        item.getQuantidadeAjustada()))
                .collect(Collectors.toList());
        historicoRegistros.add(new RegistroHistoricoPrevisao(tipoRegistro, usuarioResponsavelId, observacao, snapshot));
    }

    private String normalizar(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getGeradoPorUsuarioId() {
        return geradoPorUsuarioId;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public StatusHistoricoPrevisao getStatusHistorico() {
        return statusHistorico;
    }

    public int getTotalEventosBase() {
        return totalEventosBase;
    }

    public List<ItemPrevisao> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public List<RegistroHistoricoPrevisao> getHistoricoRegistros() {
        return Collections.unmodifiableList(historicoRegistros);
    }

    public TipoEvento getTipoEventoReferencia() {
        return tipoEventoReferencia;
    }

    public PorteEvento getPorteEventoReferencia() {
        return porteEventoReferencia;
    }

    public int getQuantidadeParticipantesReferencia() {
        return quantidadeParticipantesReferencia;
    }

    public String getObjetivoReferencia() {
        return objetivoReferencia;
    }
}

package domain.conciliacao.entity;

import domain.conciliacao.valueobject.ItemRelatorioConciliacao;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RelatorioConciliacao {

    private final String id;
    private final String eventoId;
    private final String responsavelId;
    private final LocalDateTime dataGeracao;
    private final List<ItemRelatorioConciliacao> itens;

    public RelatorioConciliacao(String eventoId, String responsavelId, List<ItemRelatorioConciliacao> itens) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (responsavelId == null || responsavelId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do responsável é obrigatório.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O relatório deve conter ao menos um item.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.responsavelId = responsavelId;
        this.dataGeracao = LocalDateTime.now();
        this.itens = Collections.unmodifiableList(itens);
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getResponsavelId() { return responsavelId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public List<ItemRelatorioConciliacao> getItens() { return itens; }
}

package domain.financeiro.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class RelatorioFinanceiro {
    private final String id;
    private final String eventoId;
    private final LocalDateTime dataGeracao;
    private final String geradoPorUsuarioId;
    private final String conteudo;

    public RelatorioFinanceiro() {
        this.id = UUID.randomUUID().toString();
        this.eventoId = null;
        this.geradoPorUsuarioId = null;
        this.conteudo = null;
        this.dataGeracao = LocalDateTime.now();
    }

    public RelatorioFinanceiro(String eventoId, String geradoPorUsuarioId, String conteudo) {
        if (eventoId == null || geradoPorUsuarioId == null) {
            throw new IllegalArgumentException("IDs de evento e usuário são obrigatórios para o relatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.conteudo = conteudo;
        this.dataGeracao = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public String getConteudo() { return conteudo; }
}

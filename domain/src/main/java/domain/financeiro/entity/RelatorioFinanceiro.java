package domain.financeiro.entity;

import domain.financeiro.valueobject.ItemRelatorioCategoria;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RelatorioFinanceiro {

    private final String id;
    private final String eventoId;
    private final String geradoPorUsuarioId;
    private final LocalDateTime dataGeracao;
    private final BigDecimal totalGeralPrevisto;
    private final BigDecimal totalGeralRealizado;
    private final List<ItemRelatorioCategoria> itensPorCategoria;
    private final String conteudo;

    public RelatorioFinanceiro(String eventoId,
                                String geradoPorUsuarioId,
                                BigDecimal totalGeralPrevisto,
                                BigDecimal totalGeralRealizado,
                                List<ItemRelatorioCategoria> itensPorCategoria,
                                String conteudo) {

        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do evento é obrigatório.");
        }
        if (geradoPorUsuarioId == null || geradoPorUsuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("Usuário responsável pela geração é obrigatório.");
        }
        if (itensPorCategoria == null || itensPorCategoria.isEmpty()) {
            throw new IllegalArgumentException(
                    "O relatório deve conter ao menos um item por categoria.");
        }

        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.geradoPorUsuarioId = geradoPorUsuarioId;
        this.dataGeracao = LocalDateTime.now();
        this.totalGeralPrevisto = totalGeralPrevisto != null ? totalGeralPrevisto : BigDecimal.ZERO;
        this.totalGeralRealizado = totalGeralRealizado != null ? totalGeralRealizado : BigDecimal.ZERO;
        this.itensPorCategoria = Collections.unmodifiableList(itensPorCategoria);
        this.conteudo = conteudo;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getGeradoPorUsuarioId() { return geradoPorUsuarioId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public BigDecimal getTotalGeralPrevisto() { return totalGeralPrevisto; }
    public BigDecimal getTotalGeralRealizado() { return totalGeralRealizado; }
    public List<ItemRelatorioCategoria> getItensPorCategoria() { return itensPorCategoria; }
    public String getConteudo() { return conteudo; }
}

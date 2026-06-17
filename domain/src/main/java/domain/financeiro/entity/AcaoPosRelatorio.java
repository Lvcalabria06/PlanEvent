package domain.financeiro.entity;

import domain.financeiro.valueobject.StatusAcaoPosRelatorio;
import domain.financeiro.valueobject.TipoRecomendacaoFinanceira;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de ação derivada de uma recomendação financeira, vinculada a um
 * relatório persistido sem alterar seu snapshot (RN18 da Funcionalidade 2).
 */
public class AcaoPosRelatorio {

    private final String id;
    private final String relatorioId;
    private final TipoRecomendacaoFinanceira tipoRecomendacao;
    private final String descricao;
    private StatusAcaoPosRelatorio status;
    private final LocalDateTime criadaEm;
    private LocalDateTime tratadaEm;

    public AcaoPosRelatorio(String relatorioId,
                             TipoRecomendacaoFinanceira tipoRecomendacao,
                             String descricao) {
        if (relatorioId == null || relatorioId.isBlank()) {
            throw new IllegalArgumentException("ID do relatório é obrigatório.");
        }
        if (tipoRecomendacao == null) {
            throw new IllegalArgumentException("Tipo da recomendação é obrigatório.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição da ação é obrigatória.");
        }
        this.id = UUID.randomUUID().toString();
        this.relatorioId = relatorioId;
        this.tipoRecomendacao = tipoRecomendacao;
        this.descricao = descricao;
        this.status = StatusAcaoPosRelatorio.PENDENTE;
        this.criadaEm = LocalDateTime.now();
    }

    public void marcarComoTratada() {
        if (this.status == StatusAcaoPosRelatorio.TRATADA) {
            throw new IllegalStateException("Ação já foi marcada como tratada.");
        }
        this.status = StatusAcaoPosRelatorio.TRATADA;
        this.tratadaEm = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getRelatorioId() { return relatorioId; }
    public TipoRecomendacaoFinanceira getTipoRecomendacao() { return tipoRecomendacao; }
    public String getDescricao() { return descricao; }
    public StatusAcaoPosRelatorio getStatus() { return status; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getTratadaEm() { return tratadaEm; }
}

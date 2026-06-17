package domain.evento.planejamento;

import domain.evento.valueobject.ClassificacaoAlocacaoLocal;

import java.math.BigDecimal;

public class CandidatoAnaliseLocal {

    private final String localId;
    private final String nomeLocal;
    private final ClassificacaoAlocacaoLocal classificacao;
    private final String justificativa;
    private final BigDecimal custo;
    private final boolean acimaDoTeto;
    private final int capacidade;
    private final boolean capacidadeOk;
    private final boolean agendaOk;

    public CandidatoAnaliseLocal(
            String localId,
            String nomeLocal,
            ClassificacaoAlocacaoLocal classificacao,
            String justificativa,
            BigDecimal custo,
            int capacidade,
            boolean acimaDoTeto,
            boolean capacidadeOk,
            boolean agendaOk) {
        this.localId = localId;
        this.nomeLocal = nomeLocal;
        this.classificacao = classificacao;
        this.justificativa = justificativa;
        this.custo = custo;
        this.capacidade = capacidade;
        this.acimaDoTeto = acimaDoTeto;
        this.capacidadeOk = capacidadeOk;
        this.agendaOk = agendaOk;
    }

    public String getLocalId() {
        return localId;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public ClassificacaoAlocacaoLocal getClassificacao() {
        return classificacao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public boolean isAcimaDoTeto() {
        return acimaDoTeto;
    }

    public boolean isCapacidadeOk() {
        return capacidadeOk;
    }

    public boolean isAgendaOk() {
        return agendaOk;
    }

    public boolean podeSerPrincipal() {
        return agendaOk
                && (classificacao == ClassificacaoAlocacaoLocal.RECOMENDADO
                || classificacao == ClassificacaoAlocacaoLocal.VIAVEL_COM_RESSALVA);
    }
}

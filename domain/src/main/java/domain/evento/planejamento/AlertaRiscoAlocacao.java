package domain.evento.planejamento;

import domain.evento.valueobject.MotivoAlertaAlocacao;

import java.util.List;

public class AlertaRiscoAlocacao {

    private final String eventoId;
    private final String localPrincipalId;
    private final String descricao;
    private final List<MotivoAlertaAlocacao> motivos;
    private final String melhorSubstitutoSugeridoId;

    public AlertaRiscoAlocacao(
            String eventoId,
            String localPrincipalId,
            String descricao,
            List<MotivoAlertaAlocacao> motivos,
            String melhorSubstitutoSugeridoId) {
        this.eventoId = eventoId;
        this.localPrincipalId = localPrincipalId;
        this.descricao = descricao;
        this.motivos = List.copyOf(motivos);
        this.melhorSubstitutoSugeridoId = melhorSubstitutoSugeridoId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getLocalPrincipalId() {
        return localPrincipalId;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<MotivoAlertaAlocacao> getMotivos() {
        return motivos;
    }

    public String getMelhorSubstitutoSugeridoId() {
        return melhorSubstitutoSugeridoId;
    }
}

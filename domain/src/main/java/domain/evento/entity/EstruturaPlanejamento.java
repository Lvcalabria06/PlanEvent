package domain.evento.entity;

import java.util.UUID;

public class EstruturaPlanejamento {
    private final String id;
    private final String planejamentoId;
    private String cronogramaInicial;
    private String sugestaoEquipe;
    private String sugestaoRecursos;
    private String requisitosOperacionais;

    public EstruturaPlanejamento() {
        this.id = UUID.randomUUID().toString();
        this.planejamentoId = null;
    }

    public EstruturaPlanejamento(String planejamentoId, String cronogramaInicial, String sugestaoEquipe, String sugestaoRecursos, String requisitosOperacionais) {
        if (planejamentoId == null) {
            throw new IllegalArgumentException("O ID do planejamento é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.planejamentoId = planejamentoId;
        this.cronogramaInicial = cronogramaInicial;
        this.sugestaoEquipe = sugestaoEquipe;
        this.sugestaoRecursos = sugestaoRecursos;
        this.requisitosOperacionais = requisitosOperacionais;
    }
    
    public void atualizarCronograma(String novoCronograma) {
        this.cronogramaInicial = novoCronograma;
    }

    // Getters
    public String getId() { return id; }
    public String getPlanejamentoId() { return planejamentoId; }
    public String getCronogramaInicial() { return cronogramaInicial; }
    public String getSugestaoEquipe() { return sugestaoEquipe; }
    public String getSugestaoRecursos() { return sugestaoRecursos; }
    public String getRequisitosOperacionais() { return requisitosOperacionais; }
}

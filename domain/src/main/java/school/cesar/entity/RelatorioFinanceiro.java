package school.cesar.entity;

import java.time.LocalDateTime;

public class RelatorioFinanceiro {
    private String id;
    private String eventoId; // FK
    private LocalDateTime dataGeracao;
    private String usuarioResponsavelId; // FK
    private String dados; // JSON string
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RelatorioFinanceiro() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public String getUsuarioResponsavelId() { return usuarioResponsavelId; }
    public void setUsuarioResponsavelId(String usuarioResponsavelId) { this.usuarioResponsavelId = usuarioResponsavelId; }

    public String getDados() { return dados; }
    public void setDados(String dados) { this.dados = dados; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

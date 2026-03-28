package school.cesar.entity;

import java.time.LocalDateTime;

public class VersaoContrato {
    private String id;
    private String contratoId; // FK
    private String arquivo;
    private LocalDateTime dataUpload;
    private String usuarioResponsavelId; // FK
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VersaoContrato() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getContratoId() { return contratoId; }
    public void setContratoId(String contratoId) { this.contratoId = contratoId; }

    public String getArquivo() { return arquivo; }
    public void setArquivo(String arquivo) { this.arquivo = arquivo; }

    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

    public String getUsuarioResponsavelId() { return usuarioResponsavelId; }
    public void setUsuarioResponsavelId(String usuarioResponsavelId) { this.usuarioResponsavelId = usuarioResponsavelId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package domain.local.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ManutencaoLocal {
    private final String id;
    private final String localId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String responsavel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ManutencaoLocal(String localId, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel) {
        validarCampos(localId, dataInicio, dataFim, responsavel);
        
        this.id = UUID.randomUUID().toString();
        this.localId = localId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.responsavel = responsavel;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void atualizar(LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel) {
        validarCampos(this.localId, dataInicio, dataFim, responsavel);
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.responsavel = responsavel;
        this.updatedAt = LocalDateTime.now();
    }

    private void validarCampos(String localId, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel) {
        if (localId == null || localId.trim().isEmpty()) {
            throw new IllegalArgumentException("O ID do local é obrigatório.");
        }
        if (responsavel == null || responsavel.trim().isEmpty()) {
            throw new IllegalArgumentException("O usuário responsável é obrigatório.");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
    }

    public String getId() { return id; }
    public String getLocalId() { return localId; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getResponsavel() { return responsavel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

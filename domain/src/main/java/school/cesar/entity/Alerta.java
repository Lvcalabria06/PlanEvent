package school.cesar.entity;

import school.cesar.entity.enums.*;
import java.time.LocalDateTime;

public class Alerta {
    private String id;
    private String tarefaId; // FK
    private TipoAlerta tipo;
    private LocalDateTime dataGeracao;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Alerta() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTarefaId() { return tarefaId; }
    public void setTarefaId(String tarefaId) { this.tarefaId = tarefaId; }

    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

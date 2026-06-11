package infrastructure.persistence.contrato.entity;

import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrato")
public class ContratoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "fornecedor_id", nullable = false)
    private String fornecedorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoContrato tipo;

    @Column(name = "objeto", nullable = false)
    private String objeto;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusContrato status;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParteContratoJpaEntity> partes = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ContratoJpaEntity() {}

    public ContratoJpaEntity(String id, String eventoId, String fornecedorId, TipoContrato tipo,
                             String objeto, BigDecimal valor, LocalDateTime dataInicio,
                             LocalDateTime dataFim, StatusContrato status,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.eventoId = eventoId;
        this.fornecedorId = fornecedorId;
        this.tipo = tipo;
        this.objeto = objeto;
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getFornecedorId() { return fornecedorId; }
    public TipoContrato getTipo() { return tipo; }
    public String getObjeto() { return objeto; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusContrato getStatus() { return status; }
    public List<ParteContratoJpaEntity> getPartes() { return partes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

package infrastructure.persistence.financeiro.entity;

import domain.financeiro.valueobject.CategoriaDespesa;
import domain.financeiro.valueobject.StatusDespesa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "despesa")
public class DespesaJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaDespesa categoria;

    @Column(name = "fornecedor_id", nullable = false)
    private String fornecedorId;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "lancado_por_usuario_id", nullable = false)
    private String lancadoPorUsuarioId;

    @Column(name = "data_hora_lancamento", nullable = false)
    private LocalDateTime dataHoraLancamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusDespesa status;

    @Column(name = "aprovador_id")
    private String aprovadorId;

    @Column(name = "motivo_rejeicao")
    private String motivoRejeicao;

    protected DespesaJpaEntity() {
    }

    public DespesaJpaEntity(String id, String eventoId, CategoriaDespesa categoria,
                            String fornecedorId, BigDecimal valor, LocalDateTime data,
                            String lancadoPorUsuarioId, LocalDateTime dataHoraLancamento,
                            StatusDespesa status, String aprovadorId, String motivoRejeicao) {
        this.id = id;
        this.eventoId = eventoId;
        this.categoria = categoria;
        this.fornecedorId = fornecedorId;
        this.valor = valor;
        this.data = data;
        this.lancadoPorUsuarioId = lancadoPorUsuarioId;
        this.dataHoraLancamento = dataHoraLancamento;
        this.status = status;
        this.aprovadorId = aprovadorId;
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public CategoriaDespesa getCategoria() { return categoria; }
    public String getFornecedorId() { return fornecedorId; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getData() { return data; }
    public String getLancadoPorUsuarioId() { return lancadoPorUsuarioId; }
    public LocalDateTime getDataHoraLancamento() { return dataHoraLancamento; }
    public StatusDespesa getStatus() { return status; }
    public String getAprovadorId() { return aprovadorId; }
    public String getMotivoRejeicao() { return motivoRejeicao; }
}

package infrastructure.persistence.contrato.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "parte_contrato")
public class ParteContratoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoJpaEntity contrato;

    @Column(name = "nome_parte", nullable = false)
    private String nomeParte;

    @Column(name = "tipo_parte", nullable = false)
    private String tipoParte;

    protected ParteContratoJpaEntity() {}

    public ParteContratoJpaEntity(String id, ContratoJpaEntity contrato,
                                  String nomeParte, String tipoParte) {
        this.id = id;
        this.contrato = contrato;
        this.nomeParte = nomeParte;
        this.tipoParte = tipoParte;
    }

    public String getId() { return id; }
    public ContratoJpaEntity getContrato() { return contrato; }
    public String getNomeParte() { return nomeParte; }
    public String getTipoParte() { return tipoParte; }
}

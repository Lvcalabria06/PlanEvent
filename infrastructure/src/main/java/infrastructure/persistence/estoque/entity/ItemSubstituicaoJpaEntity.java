package infrastructure.persistence.estoque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_substituicao")
public class ItemSubstituicaoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "item_original_id", nullable = false)
    private String itemOriginalId;

    @Column(name = "item_substituto_id", nullable = false)
    private String itemSubstitutoId;

    @Column(name = "fator_equivalencia", nullable = false)
    private double fatorEquivalencia;

    protected ItemSubstituicaoJpaEntity() {
    }

    public ItemSubstituicaoJpaEntity(String id, String itemOriginalId, String itemSubstitutoId,
                                     double fatorEquivalencia) {
        this.id = id;
        this.itemOriginalId = itemOriginalId;
        this.itemSubstitutoId = itemSubstitutoId;
        this.fatorEquivalencia = fatorEquivalencia;
    }

    public String getId() {
        return id;
    }

    public String getItemOriginalId() {
        return itemOriginalId;
    }

    public String getItemSubstitutoId() {
        return itemSubstitutoId;
    }

    public double getFatorEquivalencia() {
        return fatorEquivalencia;
    }
}

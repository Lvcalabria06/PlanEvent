package infrastructure.persistence.estoque.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consumo_evento")
public class ConsumoEventoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "registrado_por_usuario_id", nullable = false)
    private String registradoPorUsuarioId;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "valido", nullable = false)
    private boolean valido;

    @OneToMany(mappedBy = "consumoEvento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemConsumoEventoJpaEntity> itensConsumidos = new ArrayList<>();

    protected ConsumoEventoJpaEntity() {
    }

    public ConsumoEventoJpaEntity(String id, String eventoId, String registradoPorUsuarioId,
                                  LocalDateTime dataRegistro, boolean valido) {
        this.id = id;
        this.eventoId = eventoId;
        this.registradoPorUsuarioId = registradoPorUsuarioId;
        this.dataRegistro = dataRegistro;
        this.valido = valido;
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getRegistradoPorUsuarioId() {
        return registradoPorUsuarioId;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public boolean isValido() {
        return valido;
    }

    public List<ItemConsumoEventoJpaEntity> getItensConsumidos() {
        return itensConsumidos;
    }

    public void setItensConsumidos(List<ItemConsumoEventoJpaEntity> itensConsumidos) {
        this.itensConsumidos = itensConsumidos != null ? new ArrayList<>(itensConsumidos) : new ArrayList<>();
    }
}

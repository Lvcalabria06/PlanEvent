package school.cesar.entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class Local {
    private String id;
    private String nome;
    private Integer capacidade;
    private BigDecimal custo;
    private String descricao;
    
    // Associacoes de 1:N que fazem sentido no Agregado Raiz (Local)
    private List<DisponibilidadeLocal> disponibilidades = new ArrayList<>();
    private List<ReservaLocal> reservas = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Local() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<DisponibilidadeLocal> getDisponibilidades() { return disponibilidades; }
    public void setDisponibilidades(List<DisponibilidadeLocal> disponibilidades) { this.disponibilidades = disponibilidades; }

    public List<ReservaLocal> getReservas() { return reservas; }
    public void setReservas(List<ReservaLocal> reservas) { this.reservas = reservas; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

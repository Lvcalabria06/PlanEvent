package infrastructure.persistence.equipe.entity;

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
@Table(name = "equipe")
public class EquipeJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "evento_id", nullable = false)
    private String eventoId;

    @Column(name = "nome", nullable = false)
    private String nome;

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MembroEquipeJpaEntity> membros = new ArrayList<>();

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    protected EquipeJpaEntity() {
    }

    public EquipeJpaEntity(String id, String eventoId, String nome, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.eventoId = eventoId;
        this.nome = nome;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getNome() {
        return nome;
    }

    public List<MembroEquipeJpaEntity> getMembros() {
        return membros;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setMembros(List<MembroEquipeJpaEntity> membros) {
        this.membros = membros != null ? new ArrayList<>(membros) : new ArrayList<>();
    }
}

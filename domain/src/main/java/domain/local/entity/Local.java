package domain.local.entity;

import domain.local.valueobject.StatusLocal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Local {
    private final String id;
    private String nome;
    private int capacidade;
    private String endereco;
    private String tipo;
    private String infraestrutura;
    private String restricoes;
    private BigDecimal custo;
    private StatusLocal status;
    private LocalDateTime updatedAt;

    // Construtor principal para novos locais
    public Local(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        validarCamposObrigatorios(nome, capacidade, endereco, tipo, infraestrutura);
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.capacidade = capacidade;
        this.endereco = endereco;
        this.tipo = tipo;
        this.infraestrutura = infraestrutura;
        this.custo = custo != null ? custo : BigDecimal.ZERO;
        this.status = StatusLocal.ATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    // Método para atualizar os dados de um local existente (RN8 e RN3)
    public void atualizarDados(String nome, int capacidade, String endereco, String tipo, String infraestrutura, BigDecimal custo) {
        validarCamposObrigatorios(nome, capacidade, endereco, tipo, infraestrutura);
        this.nome = nome;
        this.capacidade = capacidade;
        this.endereco = endereco;
        this.tipo = tipo;
        this.infraestrutura = infraestrutura;
        this.custo = custo != null ? custo : BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    private void validarCamposObrigatorios(String nome, int capacidade, String endereco, String tipo, String infraestrutura) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do local é obrigatório.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do local é obrigatório.");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo do local é obrigatório.");
        }
        if (infraestrutura == null || infraestrutura.trim().isEmpty()) {
            throw new IllegalArgumentException("A infraestrutura do local é obrigatória.");
        }
    }

    public void adicionarRestricoes(String restricoes) {
        this.restricoes = restricoes;
        this.updatedAt = LocalDateTime.now();
    }

    // RN6 e RN8
    public void desativar() {
        this.status = StatusLocal.INATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    // RN7
    public boolean isAtivo() {
        return this.status == StatusLocal.ATIVO;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public String getEndereco() { return endereco; }
    public String getTipo() { return tipo; }
    public String getInfraestrutura() { return infraestrutura; }
    public String getRestricoes() { return restricoes; }
    public BigDecimal getCusto() { return custo; }
    public StatusLocal getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

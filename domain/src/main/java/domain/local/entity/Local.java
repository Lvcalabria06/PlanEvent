package domain.local.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class Local {
    private final String id;
    private String nome;
    private int capacidade;
    private String infraestrutura;
    private String restricoes;
    private BigDecimal custo;

    public Local() {
        this.id = UUID.randomUUID().toString();
    }

    public Local(String nome, int capacidade, BigDecimal custo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do local é obrigatório.");
        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("A capacidade deve ser maior que zero.");
        }
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.capacidade = capacidade;
        this.custo = custo != null ? custo : BigDecimal.ZERO;
    }

    public void atualizarInfraestrutura(String infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public void adicionarRestricoes(String restricoes) {
        this.restricoes = restricoes;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }
    public String getInfraestrutura() { return infraestrutura; }
    public String getRestricoes() { return restricoes; }
    public BigDecimal getCusto() { return custo; }
}

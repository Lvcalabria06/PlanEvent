package domain.estoque.entity;

import java.util.UUID;

public class ItemEstoque {
    private final String id;
    private String nome;
    private int quantidadeTotal;
    private int quantidadeDisponivel;

    public ItemEstoque() {
        this.id = UUID.randomUUID().toString();
    }

    public ItemEstoque(String nome, int quantidadeTotal) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item é obrigatório.");
        }
        if (quantidadeTotal < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
    }

    public void reservar(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a reservar deve ser maior que zero.");
        }
        if (quantidade > this.quantidadeDisponivel) {
            throw new IllegalStateException("Quantidade insuficiente em estoque para reserva.");
        }
        this.quantidadeDisponivel -= quantidade;
    }

    public void liberarReserva(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a liberar deve ser maior que zero.");
        }
        this.quantidadeDisponivel += quantidade;
        if (this.quantidadeDisponivel > this.quantidadeTotal) {
            this.quantidadeDisponivel = this.quantidadeTotal;
        }
    }

    public void adicionarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a adicionar deve ser maior que zero.");
        }
        this.quantidadeTotal += quantidade;
        this.quantidadeDisponivel += quantidade;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidadeTotal() { return quantidadeTotal; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
}

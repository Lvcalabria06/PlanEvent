package domain.estoque.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItemEstoque {
    private final String id;
    private String nome;
    private int quantidadeTotal;
    private int quantidadeDisponivel;
    private boolean ativo;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ItemEstoque() {
        this.id = UUID.randomUUID().toString();
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public ItemEstoque(String nome, int quantidadeTotal) {
        this(UUID.randomUUID().toString(), nome, quantidadeTotal);
    }

    public ItemEstoque(String id, String nome, int quantidadeTotal) {
        validar(nome, quantidadeTotal);
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID do item de estoque e obrigatorio.");
        }
        this.id = id;
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeTotal;
        this.ativo = true;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void atualizarDados(String nome, int quantidadeTotal) {
        garantirAtivo();
        validar(nome, quantidadeTotal);
        int diferencaTotal = quantidadeTotal - this.quantidadeTotal;
        this.nome = nome;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = Math.max(0, this.quantidadeDisponivel + diferencaTotal);
        if (this.quantidadeDisponivel > this.quantidadeTotal) {
            this.quantidadeDisponivel = this.quantidadeTotal;
        }
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void reservar(int quantidade) {
        garantirAtivo();
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a reservar deve ser maior que zero.");
        }
        if (quantidade > this.quantidadeDisponivel) {
            throw new IllegalStateException("Quantidade insuficiente em estoque para reserva.");
        }
        this.quantidadeDisponivel -= quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void liberarReserva(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a liberar deve ser maior que zero.");
        }
        this.quantidadeDisponivel += quantidade;
        if (this.quantidadeDisponivel > this.quantidadeTotal) {
            this.quantidadeDisponivel = this.quantidadeTotal;
        }
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void adicionarEstoque(int quantidade) {
        garantirAtivo();
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a adicionar deve ser maior que zero.");
        }
        this.quantidadeTotal += quantidade;
        this.quantidadeDisponivel += quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void desativar() {
        if (!this.ativo) {
            throw new IllegalStateException("Item ja esta inativo.");
        }
        this.ativo = false;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void reativar() {
        if (this.ativo) {
            throw new IllegalStateException("Item ja esta ativo.");
        }
        this.ativo = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    private void validar(String nome, int quantidadeTotal) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do item e obrigatorio.");
        }
        if (quantidadeTotal < 0) {
            throw new IllegalArgumentException("A quantidade nao pode ser negativa.");
        }
    }

    private void garantirAtivo() {
        if (!this.ativo) {
            throw new IllegalStateException("Operacao nao permitida em item inativo.");
        }
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidadeTotal() { return quantidadeTotal; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}

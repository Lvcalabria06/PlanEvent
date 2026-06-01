package domain.fornecedor.entity;

import domain.fornecedor.util.CnpjValidator;
import domain.fornecedor.valueobject.StatusFornecedor;

import java.time.LocalDateTime;
import java.util.UUID;

public class Fornecedor {
    private final String id;
    private String nome;
    private String cnpj;
    private String categoriaServico;
    private String contato;
    private StatusFornecedor status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Fornecedor(String nome, String cnpj, String categoriaServico, String contato) {
        validarCamposObrigatorios(nome, cnpj, categoriaServico, contato);
        this.id = UUID.randomUUID().toString();
        this.nome = nome.trim();
        this.cnpj = CnpjValidator.normalizar(cnpj);
        this.categoriaServico = categoriaServico.trim();
        this.contato = contato.trim();
        this.status = StatusFornecedor.ATIVO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void atualizarDados(String nome, String cnpj, String categoriaServico, String contato) {
        if (!isAtivo()) {
            throw new IllegalStateException("Fornecedor inativo não pode ser editado.");
        }
        validarCamposObrigatorios(nome, cnpj, categoriaServico, contato);
        this.nome = nome.trim();
        this.cnpj = CnpjValidator.normalizar(cnpj);
        this.categoriaServico = categoriaServico.trim();
        this.contato = contato.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public void desativar() {
        this.status = StatusFornecedor.INATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAtivo() {
        return this.status == StatusFornecedor.ATIVO;
    }

    private static void validarCamposObrigatorios(String nome, String cnpj, String categoriaServico, String contato) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do fornecedor é obrigatório.");
        }
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("O CNPJ do fornecedor é obrigatório.");
        }
        if (!CnpjValidator.isValido(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido.");
        }
        if (categoriaServico == null || categoriaServico.trim().isEmpty()) {
            throw new IllegalArgumentException("A categoria de serviço do fornecedor é obrigatória.");
        }
        if (contato == null || contato.trim().isEmpty()) {
            throw new IllegalArgumentException("O contato do fornecedor é obrigatório.");
        }
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getCategoriaServico() { return categoriaServico; }
    public String getContato() { return contato; }
    public StatusFornecedor getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

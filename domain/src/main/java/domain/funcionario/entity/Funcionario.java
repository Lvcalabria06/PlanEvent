package domain.funcionario.entity;

import domain.funcionario.valueobject.CargoFuncionario;
import domain.funcionario.valueobject.DisponibilidadeFuncionario;

import java.time.LocalDateTime;
import java.util.UUID;

public class Funcionario {
    private final String id;
    private String nome;
    private CargoFuncionario cargo;
    private DisponibilidadeFuncionario disponibilidade;
    private boolean ativo;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Funcionario(String nome, String cargo, String disponibilidade) {
        validarNome(nome);

        this.id = UUID.randomUUID().toString();
        this.nome = nome.trim();
        this.cargo = CargoFuncionario.fromString(cargo);
        this.disponibilidade = DisponibilidadeFuncionario.fromString(disponibilidade);
        this.ativo = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void alterarNome(String novoNome) {
        validarAtivo();
        validarNome(novoNome);
        this.nome = novoNome.trim();
        atualizarData();
    }

    public void alterarCargo(String novoCargo) {
        validarAtivo();
        this.cargo = CargoFuncionario.fromString(novoCargo);
        atualizarData();
    }

    public void alterarDisponibilidade(String novaDisponibilidade) {
        validarAtivo();
        this.disponibilidade = DisponibilidadeFuncionario.fromString(novaDisponibilidade);
        atualizarData();
    }

    public void inativar() {
        if (!this.ativo) {
            throw new IllegalStateException("Funcionário já está inativo.");
        }
        this.ativo = false;
        atualizarData();
    }

    public void ativar() {
        if (this.ativo) {
            throw new IllegalStateException("Funcionário já está ativo.");
        }
        this.ativo = true;
        atualizarData();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        String nomeTratado = nome.trim();

        if (nomeTratado.length() < 3) {
            throw new IllegalArgumentException("Nome deve ter no mínimo 3 caracteres.");
        }

        if (!nomeTratado.matches("^[A-Za-zÀ-ÖØ-öø-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Nome não pode conter números ou caracteres inválidos.");
        }
    }

    private void validarAtivo() {
        if (!this.ativo) {
            throw new IllegalStateException("Operação não permitida para funcionário inativo.");
        }
    }

    private void atualizarData() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public CargoFuncionario getCargo() { return cargo; }
    public DisponibilidadeFuncionario getDisponibilidade() { return disponibilidade; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
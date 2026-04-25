package domain.evento.entity;

import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

import java.time.LocalDateTime;
import java.util.UUID;

public class Evento {
    private final String id;
    private String nome;
    private TipoEvento tipo;
    private PorteEvento porte;
    private int quantidadeEstimadaParticipantes;
    private String objetivo;
    private String localId;
    private boolean planejamentoConfirmado;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Evento() {
        this.id = UUID.randomUUID().toString();
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public Evento(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo, String localId) {
        validarDadosObrigatorios(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo);
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.tipo = tipo;
        this.porte = porte;
        this.quantidadeEstimadaParticipantes = quantidadeEstimadaParticipantes;
        this.objetivo = objetivo;
        this.localId = localId;
        this.planejamentoConfirmado = false;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void atualizarDados(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("Não é possível editar o evento após confirmar o planejamento.");
        }
        validarDadosObrigatorios(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo);
        this.nome = nome;
        this.tipo = tipo;
        this.porte = porte;
        this.quantidadeEstimadaParticipantes = quantidadeEstimadaParticipantes;
        this.objetivo = objetivo;
        this.atualizarData();
    }

    public void confirmarPlanejamento() {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("O planejamento já está confirmado.");
        }
        this.planejamentoConfirmado = true;
        this.atualizarData();
    }

    public void alterarLocal(String novoLocalId) {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("Não é possível alterar o local após confirmar o planejamento.");
        }
        this.localId = novoLocalId;
        this.atualizarData();
    }

    private void atualizarData() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    private void validarDadosObrigatorios(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do evento é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo do evento é obrigatório.");
        }
        if (porte == null) {
            throw new IllegalArgumentException("Porte do evento é obrigatório.");
        }
        if (quantidadeEstimadaParticipantes <= 0) {
            throw new IllegalArgumentException("Quantidade estimada deve ser maior que zero.");
        }
        if (objetivo == null || objetivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Objetivo do evento é obrigatório.");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoEvento getTipo() { return tipo; }
    public PorteEvento getPorte() { return porte; }
    public int getQuantidadeEstimadaParticipantes() { return quantidadeEstimadaParticipantes; }
    public String getObjetivo() { return objetivo; }
    public String getLocalId() { return localId; }
    public boolean isPlanejamentoConfirmado() { return planejamentoConfirmado; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}

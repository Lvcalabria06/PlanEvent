package domain.evento.entity;

import domain.evento.planejamento.TrocaLocalPlanejamento;
import domain.evento.valueobject.PorteEvento;
import domain.evento.valueobject.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Evento {
    private final String id;
    private String nome;
    private TipoEvento tipo;
    private PorteEvento porte;
    private int quantidadeEstimadaParticipantes;
    private String objetivo;
    private String localId;
    private String layoutLocalId;
    private String justificativaExcecaoLayout;
    private boolean validacaoLayoutPendente;
    private boolean planejamentoConfirmado;
    private boolean concluido;
    private LocalDateTime janelaInicioPlanejamento;
    private LocalDateTime janelaFimPlanejamento;
    private BigDecimal tetoCustoEspacoInformado;
    private String requisitosInfraestrutura;
    private final List<String> locaisContingenciaOrdenados;
    private final List<TrocaLocalPlanejamento> historicoTrocasLocal;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Evento() {
        this.id = UUID.randomUUID().toString();
        this.locaisContingenciaOrdenados = new ArrayList<>();
        this.historicoTrocasLocal = new ArrayList<>();
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
        this.layoutLocalId = null;
        this.justificativaExcecaoLayout = null;
        this.validacaoLayoutPendente = false;
        this.planejamentoConfirmado = false;
        this.locaisContingenciaOrdenados = new ArrayList<>();
        this.historicoTrocasLocal = new ArrayList<>();
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    public void atualizarDados(String nome, TipoEvento tipo, PorteEvento porte, int quantidadeEstimadaParticipantes, String objetivo) {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("Não é possível editar o evento após confirmar o planejamento.");
        }
        validarDadosObrigatorios(nome, tipo, porte, quantidadeEstimadaParticipantes, objetivo);
        boolean mudouParticipantes = this.quantidadeEstimadaParticipantes != quantidadeEstimadaParticipantes;
        this.nome = nome;
        this.tipo = tipo;
        this.porte = porte;
        this.quantidadeEstimadaParticipantes = quantidadeEstimadaParticipantes;
        this.objetivo = objetivo;
        if (mudouParticipantes && this.layoutLocalId != null) {
            this.validacaoLayoutPendente = true;
        }
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
        this.layoutLocalId = null;
        this.justificativaExcecaoLayout = null;
        this.validacaoLayoutPendente = false;
        this.atualizarData();
    }

    public void associarLayout(String layoutLocalId, String justificativaExcecao) {
        if (layoutLocalId == null || layoutLocalId.isBlank()) {
            throw new IllegalArgumentException("Layout do local é obrigatório.");
        }
        this.layoutLocalId = layoutLocalId;
        this.justificativaExcecaoLayout = justificativaExcecao != null && !justificativaExcecao.isBlank()
                ? justificativaExcecao.trim()
                : null;
        this.validacaoLayoutPendente = false;
        this.atualizarData();
    }

    public void marcarValidacaoLayoutPendente() {
        if (this.layoutLocalId != null) {
            this.validacaoLayoutPendente = true;
            this.atualizarData();
        }
    }

    public void definirJanelaPlanejamento(LocalDateTime inicio, LocalDateTime fim) {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("Não é possível alterar a janela após confirmar o planejamento.");
        }
        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("Janela de planejamento inválida.");
        }
        this.janelaInicioPlanejamento = inicio;
        this.janelaFimPlanejamento = fim;
        this.atualizarData();
    }

    public void definirTetoCustoEspacoInformado(BigDecimal teto) {
        if (teto == null || teto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Teto de custo deve ser maior ou igual a zero.");
        }
        this.tetoCustoEspacoInformado = teto;
        this.atualizarData();
    }

    public void definirRequisitosInfraestrutura(String requisitos) {
        if (this.planejamentoConfirmado) {
            throw new IllegalStateException("Não é possível alterar requisitos após confirmar o planejamento.");
        }
        this.requisitosInfraestrutura = requisitos != null && requisitos.isBlank() ? null : requisitos;
        this.atualizarData();
    }

    public void definirAlternativasContingenciaOrdenadas(List<String> localIdsOrdenados) {
        Objects.requireNonNull(localIdsOrdenados, "Lista de alternativas é obrigatória.");
        List<String> copia = new ArrayList<>(localIdsOrdenados);
        if (localId != null) {
            for (String id : copia) {
                if (localId.equals(id)) {
                    throw new IllegalArgumentException("Alternativa de contingência não pode repetir o local principal.");
                }
            }
        }
        this.locaisContingenciaOrdenados.clear();
        this.locaisContingenciaOrdenados.addAll(copia);
        this.atualizarData();
    }

    public void substituirLocalPrincipalPorContingenciaDocumentada(
            String novoLocalId,
            String usuarioId,
            String motivo) {
        if (!this.planejamentoConfirmado) {
            throw new IllegalStateException(
                    "Troca documentada por contingência só se aplica após confirmação da preparação inicial.");
        }
        if (novoLocalId == null || novoLocalId.isBlank()) {
            throw new IllegalArgumentException("Novo local é obrigatório.");
        }
        if (usuarioId == null || usuarioId.isBlank() || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Usuário e motivo da troca são obrigatórios.");
        }
        String anterior = this.localId;
        this.localId = novoLocalId;
        this.historicoTrocasLocal.add(new TrocaLocalPlanejamento(
                LocalDateTime.now(),
                usuarioId,
                motivo,
                anterior,
                novoLocalId));
        this.atualizarData();
    }

    public void concluirEvento() {
        if (concluido) {
            throw new IllegalStateException("O evento já está concluído.");
        }
        if (localId == null || localId.isBlank()) {
            throw new IllegalStateException("Não é possível concluir o evento sem local vinculado.");
        }
        this.concluido = true;
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
    public boolean isConcluido() { return concluido; }
    public String getLayoutLocalId() { return layoutLocalId; }
    public String getJustificativaExcecaoLayout() { return justificativaExcecaoLayout; }
    public boolean isValidacaoLayoutPendente() { return validacaoLayoutPendente; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    public LocalDateTime getJanelaInicioPlanejamento() {
        return janelaInicioPlanejamento;
    }

    public LocalDateTime getJanelaFimPlanejamento() {
        return janelaFimPlanejamento;
    }

    public BigDecimal getTetoCustoEspacoInformado() {
        return tetoCustoEspacoInformado;
    }

    public String getRequisitosInfraestrutura() {
        return requisitosInfraestrutura;
    }

    public List<String> getLocaisContingenciaOrdenados() {
        return Collections.unmodifiableList(locaisContingenciaOrdenados);
    }

    public List<TrocaLocalPlanejamento> getHistoricoTrocasLocal() {
        return Collections.unmodifiableList(historicoTrocasLocal);
    }
}

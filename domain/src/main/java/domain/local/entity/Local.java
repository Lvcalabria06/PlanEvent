package domain.local.entity;

import domain.local.valueobject.StatusLocal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    private final List<LayoutLocal> layouts;

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
        this.layouts = new ArrayList<>();
    }

    // Construtor privado para reconstituição a partir da persistência
    private Local(String id, String nome, int capacidade, String endereco, String tipo,
                  String infraestrutura, String restricoes, BigDecimal custo,
                  StatusLocal status, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.endereco = endereco;
        this.tipo = tipo;
        this.infraestrutura = infraestrutura;
        this.restricoes = restricoes;
        this.custo = custo != null ? custo : BigDecimal.ZERO;
        this.status = status;
        this.updatedAt = updatedAt;
        this.layouts = new ArrayList<>();
    }

    /** Reconstitui um local a partir de dados persistidos (sem revalidar RNs). */
    public static Local reconstituir(String id, String nome, int capacidade, String endereco,
                                     String tipo, String infraestrutura, String restricoes,
                                     BigDecimal custo, StatusLocal status, LocalDateTime updatedAt) {
        return new Local(id, nome, capacidade, endereco, tipo, infraestrutura, restricoes, custo, status, updatedAt);
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

    public LayoutLocal adicionarLayout(String nome, String descricao, int capacidadeMaxima, String usuarioResponsavel) {
        validarNomeLayoutUnico(nome, null);
        LayoutLocal layout = new LayoutLocal(nome, descricao, capacidadeMaxima, usuarioResponsavel);
        this.layouts.add(layout);
        this.updatedAt = LocalDateTime.now();
        return layout;
    }

    public LayoutLocal atualizarLayout(
            String layoutId,
            String nome,
            String descricao,
            int capacidadeMaxima,
            String usuarioResponsavel) {
        LayoutLocal layout = buscarLayoutPorId(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("Layout não encontrado para o local."));
        validarNomeLayoutUnico(nome, layoutId);
        layout.atualizar(nome, descricao, capacidadeMaxima, usuarioResponsavel);
        this.updatedAt = LocalDateTime.now();
        return layout;
    }

    public Optional<LayoutLocal> buscarLayoutPorId(String layoutId) {
        return layouts.stream().filter(l -> l.getId().equals(layoutId)).findFirst();
    }

    public List<LayoutLocal> listarLayouts() {
        return Collections.unmodifiableList(layouts);
    }

    private void validarNomeLayoutUnico(String nome, String layoutIdIgnorado) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do layout é obrigatório.");
        }
        String alvo = nome.trim().toLowerCase(Locale.ROOT);
        boolean duplicado = layouts.stream()
                .anyMatch(layout -> (layoutIdIgnorado == null || !layout.getId().equals(layoutIdIgnorado))
                        && layout.getNome().trim().toLowerCase(Locale.ROOT).equals(alvo));
        if (duplicado) {
            throw new IllegalArgumentException("Já existe layout com este nome para o local.");
        }
    }

    // RN6 and RN8
    public void desativar() {
        this.status = StatusLocal.INATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarEmManutencao() {
        this.status = StatusLocal.EM_MANUTENCAO;
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarAtivo() {
        this.status = StatusLocal.ATIVO;
        this.updatedAt = LocalDateTime.now();
    }

    // RN7
    public boolean isAtivo() {
        return this.status != StatusLocal.INATIVO;
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
    public List<LayoutLocal> getLayouts() { return Collections.unmodifiableList(layouts); }
}

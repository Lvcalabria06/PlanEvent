package domain.contrato.entity;

import domain.contrato.valueobject.NivelRisco;

import java.util.UUID;

public class RiscoContrato {
    private final String id;
    private final String contratoId;
    private NivelRisco nivelRisco;
    private String descricao;
    private boolean ativo;

    public RiscoContrato() {
        this.id = UUID.randomUUID().toString();
        this.contratoId = null;
    }

    public RiscoContrato(String contratoId, NivelRisco nivelRisco, String descricao) {
        if (contratoId == null) {
            throw new IllegalArgumentException("ID do contrato é obrigatório.");
        }
        if (nivelRisco == null) {
            throw new IllegalArgumentException("Nível de risco é obrigatório.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do risco é obrigatória.");
        }
        this.id = UUID.randomUUID().toString();
        this.contratoId = contratoId;
        this.nivelRisco = nivelRisco;
        this.descricao = descricao;
        this.ativo = true;
    }

    public void mitigar() {
        if (!this.ativo) {
            throw new IllegalStateException("O risco já não está ativo.");
        }
        this.ativo = false;
    }

    public void alterarNivelRisco(NivelRisco novoNivel) {
        this.nivelRisco = novoNivel;
    }

    // Getters
    public String getId() { return id; }
    public String getContratoId() { return contratoId; }
    public NivelRisco getNivelRisco() { return nivelRisco; }
    public String getDescricao() { return descricao; }
    public boolean isAtivo() { return ativo; }
}

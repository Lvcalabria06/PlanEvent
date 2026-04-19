package domain.contrato.entity;

import java.util.UUID;

public class ParteContrato {
    private final String id;
    private final String contratoId;
    private String nomeParte;
    private String tipoParte;

    public ParteContrato(String contratoId, String nomeParte, String tipoParte) {
        if (contratoId == null || contratoId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do contrato é obrigatório.");
        }
        if (nomeParte == null || nomeParte.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da parte é obrigatório.");
        }
        if (tipoParte == null || tipoParte.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo da parte é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.contratoId = contratoId;
        this.nomeParte = nomeParte;
        this.tipoParte = tipoParte;
    }

    public String getId() { return id; }
    public String getContratoId() { return contratoId; }
    public String getNomeParte() { return nomeParte; }
    public String getTipoParte() { return tipoParte; }
}

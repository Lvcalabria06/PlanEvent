package domain.contrato.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentoContrato {
    private final String id;
    private final String contratoId;
    private String nomeArquivo;
    private int versao;
    private final LocalDateTime dataUpload;

    public DocumentoContrato() {
        this.id = UUID.randomUUID().toString();
        this.contratoId = null;
        this.dataUpload = LocalDateTime.now();
    }

    public DocumentoContrato(String contratoId, String nomeArquivo, int versao) {
        if (contratoId == null) {
            throw new IllegalArgumentException("ID do contrato é obrigatório.");
        }
        if (nomeArquivo == null || nomeArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.contratoId = contratoId;
        this.nomeArquivo = nomeArquivo;
        this.versao = versao;
        this.dataUpload = LocalDateTime.now();
    }

    public void atualizarVersao(String novoNomeArquivo) {
        if (novoNomeArquivo == null || novoNomeArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do novo arquivo é obrigatório.");
        }
        this.nomeArquivo = novoNomeArquivo;
        this.versao++;
    }

    // Getters
    public String getId() { return id; }
    public String getContratoId() { return contratoId; }
    public String getNomeArquivo() { return nomeArquivo; }
    public int getVersao() { return versao; }
    public LocalDateTime getDataUpload() { return dataUpload; }
}

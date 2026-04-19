package domain.contrato.valueobject;

public record DadosParteContrato(String nomeParte, String tipoParte) {
    public DadosParteContrato {
        if (nomeParte == null || nomeParte.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da parte é obrigatório.");
        }
        if (tipoParte == null || tipoParte.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo da parte é obrigatório.");
        }
    }
}

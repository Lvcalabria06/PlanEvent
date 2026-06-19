package domain.funcionario.valueobject;

import java.text.Normalizer;

public enum CargoFuncionario {
    GERENTE,
    ANALISTA,
    ASSISTENTE,
    TECNICO,
    GARCOM,
    COORDENADOR,
    TECNICO_AV,
    LOGISTICA;

    public static CargoFuncionario fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }

        String normalizado = Normalizer
                .normalize(valor, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase()
                .trim();

        if (normalizado.equals("TECNICO A/V") || normalizado.equals("TECNICO AV") || normalizado.equals("TECNICO_A_V")) {
            return TECNICO_AV;
        }

        try {
            return CargoFuncionario.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cargo inválido.");
        }
    }
}
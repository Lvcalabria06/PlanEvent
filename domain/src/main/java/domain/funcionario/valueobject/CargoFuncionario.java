package domain.funcionario.valueobject;

import java.text.Normalizer;

public enum CargoFuncionario {
    GERENTE,
    ANALISTA,
    ASSISTENTE,
    TECNICO,
    GARCOM;

    public static CargoFuncionario fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }

        String normalizado = Normalizer
                .normalize(valor, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase()
                .trim();

        try {
            return CargoFuncionario.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cargo inválido.");
        }
    }
}
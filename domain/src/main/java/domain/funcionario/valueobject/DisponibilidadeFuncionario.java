package domain.funcionario.valueobject;

import java.text.Normalizer;

public enum DisponibilidadeFuncionario {
    MANHA,
    TARDE,
    NOITE,
    INTEGRAL;

    public static DisponibilidadeFuncionario fromString(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Disponibilidade é obrigatória.");
        }

        String normalizado = Normalizer
                .normalize(valor, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toUpperCase()
                .trim();

        try {
            return DisponibilidadeFuncionario.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Disponibilidade inválida.");
        }
    }
}
package domain.fornecedor.util;

public final class CnpjValidator {

    private CnpjValidator() {
    }

    public static String normalizar(String cnpj) {
        if (cnpj == null) {
            return "";
        }
        return cnpj.replaceAll("\\D", "");
    }

    public static boolean isValido(String cnpj) {
        String digits = normalizar(cnpj);
        if (digits.length() != 14) {
            return false;
        }
        if (digits.chars().distinct().count() == 1) {
            return false;
        }
        return calcularDigito(digits, 12) == Character.getNumericValue(digits.charAt(12))
                && calcularDigito(digits, 13) == Character.getNumericValue(digits.charAt(13));
    }

    private static int calcularDigito(String digits, int length) {
        int[] pesos = length == 12
                ? new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(digits.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}

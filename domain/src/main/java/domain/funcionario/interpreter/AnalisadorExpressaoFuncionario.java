package domain.funcionario.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador (parser) de expressões lógicas para filtragem de funcionários.
 * Suporta os operadores AND / OR e os campos: cargo, disponibilidade, ativo.
 * Exemplo: "cargo = tecnico AND disponibilidade = manha"
 */
public final class AnalisadorExpressaoFuncionario {

    private AnalisadorExpressaoFuncionario() {
    }

    public static ExpressaoFuncionario parse(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            throw new IllegalArgumentException("Expressão vazia ou inválida.");
        }
        return parseExpression(tokenizar(expressao.trim()));
    }

    private static List<String> tokenizar(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean dentroDeAspas = false;
        char caractereAspas = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\'' || c == '"') {
                if (dentroDeAspas) {
                    if (c == caractereAspas) {
                        dentroDeAspas = false;
                    } else {
                        sb.append(c);
                    }
                } else {
                    dentroDeAspas = true;
                    caractereAspas = c;
                }
            } else if (Character.isWhitespace(c)) {
                if (dentroDeAspas) {
                    sb.append(c);
                } else if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
            } else if ((c == '(' || c == ')') && !dentroDeAspas) {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else if (c == '=' && !dentroDeAspas) {
                if (sb.length() > 0) {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                }
                tokens.add("=");
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) {
            tokens.add(sb.toString());
        }
        return tokens;
    }

    private static ExpressaoFuncionario parseExpression(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Sintaxe de expressão inválida.");
        }

        // OR tem menor precedência — avaliado por último
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if ("OR".equalsIgnoreCase(tokens.get(i))) {
                return new ExpressaoOrFuncionario(
                        parseExpression(tokens.subList(0, i)),
                        parseExpression(tokens.subList(i + 1, tokens.size())));
            }
        }

        // AND
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if ("AND".equalsIgnoreCase(tokens.get(i))) {
                return new ExpressaoAndFuncionario(
                        parseExpression(tokens.subList(0, i)),
                        parseExpression(tokens.subList(i + 1, tokens.size())));
            }
        }

        // Expressão terminal: campo = valor
        if (tokens.size() == 3 && "=".equals(tokens.get(1))) {
            String chave = tokens.get(0).toLowerCase();
            String valor = tokens.get(2);

            return switch (chave) {
                case "cargo" -> new ExpressaoCargoFuncionario(valor);
                case "disponibilidade" -> new ExpressaoDisponibilidadeFuncionario(valor);
                case "ativo" -> new ExpressaoAtivoFuncionario(Boolean.parseBoolean(valor));
                default -> throw new IllegalArgumentException("Campo de filtro inválido: " + chave);
            };
        }

        // Parênteses
        if (tokens.size() > 2 && "(".equals(tokens.get(0)) && ")".equals(tokens.get(tokens.size() - 1))) {
            return parseExpression(tokens.subList(1, tokens.size() - 1));
        }

        throw new IllegalArgumentException("Sintaxe de expressão inválida próxima a: " + String.join(" ", tokens));
    }
}

package domain.equipe.interpreter;

import java.util.ArrayList;
import java.util.List;

public final class AnalisadorExpressaoMembro {

    private AnalisadorExpressaoMembro() {
    }

    public static ExpressaoMembro parse(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            throw new IllegalArgumentException("Expressão vazia ou inválida.");
        }

        List<String> tokens = tokenizar(expressao.trim());
        return parseExpression(tokens);
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
                } else {
                    if (sb.length() > 0) {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            } else if (c == '(' || c == ')') {
                if (dentroDeAspas) {
                    sb.append(c);
                } else {
                    if (sb.length() > 0) {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    tokens.add(String.valueOf(c));
                }
            } else if (c == '=') {
                if (dentroDeAspas) {
                    sb.append(c);
                } else {
                    if (sb.length() > 0) {
                        tokens.add(sb.toString());
                        sb.setLength(0);
                    }
                    tokens.add("=");
                }
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) {
            tokens.add(sb.toString());
        }

        return tokens;
    }

    private static ExpressaoMembro parseExpression(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Sintaxe de expressão inválida.");
        }

        for (int i = tokens.size() - 1; i >= 0; i--) {
            String token = tokens.get(i);
            if ("OR".equalsIgnoreCase(token)) {
                List<String> esquerda = tokens.subList(0, i);
                List<String> direita = tokens.subList(i + 1, tokens.size());
                return new ExpressaoOr(parseExpression(esquerda), parseExpression(direita));
            }
        }

        for (int i = tokens.size() - 1; i >= 0; i--) {
            String token = tokens.get(i);
            if ("AND".equalsIgnoreCase(token)) {
                List<String> esquerda = tokens.subList(0, i);
                List<String> direita = tokens.subList(i + 1, tokens.size());
                return new ExpressaoAnd(parseExpression(esquerda), parseExpression(direita));
            }
        }

        if (tokens.size() == 3 && "=".equals(tokens.get(1))) {
            String chave = tokens.get(0).toLowerCase();
            String valor = tokens.get(2);

            switch (chave) {
                case "lider":
                    return new ExpressaoLider(Boolean.parseBoolean(valor));
                case "cargo":
                    return new ExpressaoCargo(valor);
                case "disponibilidade":
                    return new ExpressaoDisponibilidade(valor);
                default:
                    throw new IllegalArgumentException("Campo de filtro inválido: " + chave);
            }
        }

        if (tokens.size() > 2 && "(".equals(tokens.get(0)) && ")".equals(tokens.get(tokens.size() - 1))) {
            return parseExpression(tokens.subList(1, tokens.size() - 1));
        }

        throw new IllegalArgumentException("Sintaxe de expressão inválida próxima a: " + String.join(" ", tokens));
    }
}

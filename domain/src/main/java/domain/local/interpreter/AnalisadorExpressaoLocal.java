package domain.local.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador (parser) que converte uma expressão textual em uma árvore de
 * objetos {@link ExpressaoLocal}, aplicando o padrão Interpreter (GoF).
 *
 * <h3>Campos suportados</h3>
 * <ul>
 *   <li>{@code status}         — valor: {@code ATIVO}, {@code INATIVO} ou {@code EM_MANUTENCAO}</li>
 *   <li>{@code tipo}           — valor: qualquer string (aspas simples ou duplas são opcionais)</li>
 *   <li>{@code capacidade_min} — valor: inteiro não-negativo (semântica: capacidade &gt;= N)</li>
 *   <li>{@code capacidade_max} — valor: inteiro não-negativo (semântica: capacidade &lt;= N)</li>
 * </ul>
 *
 * <h3>Operadores lógicos</h3>
 * <ul>
 *   <li>{@code AND} — conjunção (avaliado após OR, maior precedência)</li>
 *   <li>{@code OR}  — disjunção (avaliado por último, menor precedência)</li>
 * </ul>
 *
 * <h3>Exemplos</h3>
 * <pre>
 *   status = ATIVO
 *   tipo = Salão
 *   capacidade_min = 100
 *   status = ATIVO AND capacidade_min = 50
 *   (status = ATIVO OR status = EM_MANUTENCAO) AND capacidade_max = 300
 * </pre>
 */
public final class AnalisadorExpressaoLocal {

    private AnalisadorExpressaoLocal() {
    }

    /**
     * Analisa a expressão textual e retorna a árvore de {@link ExpressaoLocal}
     * correspondente.
     *
     * @param expressao string de filtro (não nula e não vazia)
     * @return raiz da árvore de expressões
     * @throws IllegalArgumentException se a expressão for inválida ou vazia
     */
    public static ExpressaoLocal parse(String expressao) {
        if (expressao == null || expressao.trim().isEmpty()) {
            throw new IllegalArgumentException("Expressão de filtro vazia ou inválida.");
        }
        List<String> tokens = tokenizar(expressao.trim());
        return parseExpressao(tokens);
    }

    // ── Tokenizador ─────────────────────────────────────────────────────────

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
                        // Fecha aspas — não inclui o delimitador, apenas o conteúdo
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

    // ── Parser recursivo ─────────────────────────────────────────────────────

    /**
     * Ponto de entrada recursivo. Avalia OR com menor precedência (procura
     * da direita para a esquerda), depois AND, depois terminais.
     */
    private static ExpressaoLocal parseExpressao(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Sintaxe de expressão inválida: sequência vazia.");
        }

        // OR tem menor precedência — busca da direita para a esquerda
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if ("OR".equalsIgnoreCase(tokens.get(i))) {
                List<String> esquerda = tokens.subList(0, i);
                List<String> direita  = tokens.subList(i + 1, tokens.size());
                return new ExpressaoOr(parseExpressao(esquerda), parseExpressao(direita));
            }
        }

        // AND tem maior precedência — busca da direita para a esquerda
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if ("AND".equalsIgnoreCase(tokens.get(i))) {
                List<String> esquerda = tokens.subList(0, i);
                List<String> direita  = tokens.subList(i + 1, tokens.size());
                return new ExpressaoAnd(parseExpressao(esquerda), parseExpressao(direita));
            }
        }

        // Expressão terminal: campo = valor
        if (tokens.size() == 3 && "=".equals(tokens.get(1))) {
            String campo = tokens.get(0).toLowerCase();
            String valor = tokens.get(2);
            return criarTerminal(campo, valor);
        }

        // Agrupamento entre parênteses: ( <expressão> )
        if (tokens.size() >= 3
                && "(".equals(tokens.get(0))
                && ")".equals(tokens.get(tokens.size() - 1))) {
            return parseExpressao(tokens.subList(1, tokens.size() - 1));
        }

        throw new IllegalArgumentException(
                "Sintaxe de expressão inválida próxima a: " + String.join(" ", tokens));
    }

    private static ExpressaoLocal criarTerminal(String campo, String valor) {
        switch (campo) {
            case "status":
                return new ExpressaoStatus(valor);
            case "tipo":
                return new ExpressaoTipo(valor);
            case "capacidade_min":
                return new ExpressaoCapacidadeMinima(valor);
            case "capacidade_max":
                return new ExpressaoCapacidadeMaxima(valor);
            default:
                throw new IllegalArgumentException(
                        "Campo de filtro inválido: \"" + campo
                        + "\". Campos suportados: status, tipo, capacidade_min, capacidade_max.");
        }
    }
}

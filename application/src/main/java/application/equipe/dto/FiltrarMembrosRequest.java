package application.equipe.dto;

/**
 * Dados de entrada para filtrar membros de uma equipe via expressão lógica (Interpreter).
 */
public record FiltrarMembrosRequest(String expressao) {
}

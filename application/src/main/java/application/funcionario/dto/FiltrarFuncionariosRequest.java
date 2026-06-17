package application.funcionario.dto;

/**
 * Dados de entrada para filtrar funcionários via expressão lógica (Interpreter).
 * Exemplo: "cargo = tecnico AND disponibilidade = manha"
 */
public record FiltrarFuncionariosRequest(String expressao) {
}

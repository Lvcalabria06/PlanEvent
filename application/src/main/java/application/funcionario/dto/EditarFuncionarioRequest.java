package application.funcionario.dto;

/**
 * Dados de entrada para editar um funcionário existente.
 */
public record EditarFuncionarioRequest(
        String nome,
        String cargo,
        String disponibilidade) {
}

package application.funcionario.dto;

/**
 * Dados de entrada para cadastrar um novo funcionário.
 */
public record CadastrarFuncionarioRequest(
        String nome,
        String cargo,
        String disponibilidade,
        java.util.List<String> competencias) {
}

package application.fornecedor.dto;

public record CadastrarFornecedorRequest(
        String nome,
        String cnpj,
        String categoriaServico,
        String contato
) {}

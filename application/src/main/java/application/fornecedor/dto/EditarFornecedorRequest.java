package application.fornecedor.dto;

public record EditarFornecedorRequest(
        String nome,
        String cnpj,
        String categoriaServico,
        String contato
) {}

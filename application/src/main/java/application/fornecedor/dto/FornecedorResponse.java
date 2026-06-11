package application.fornecedor.dto;

import domain.fornecedor.valueobject.StatusFornecedor;

import java.time.LocalDateTime;

public record FornecedorResponse(
        String id,
        String nome,
        String cnpj,
        String categoriaServico,
        String contato,
        StatusFornecedor status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

package application.fornecedor.mapper;

import application.fornecedor.dto.FornecedorResponse;
import domain.fornecedor.entity.Fornecedor;

public final class FornecedorDtoMapper {

    private FornecedorDtoMapper() {}

    public static FornecedorResponse paraResposta(Fornecedor fornecedor) {
        return new FornecedorResponse(
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getCnpj(),
                fornecedor.getCategoriaServico(),
                fornecedor.getContato(),
                fornecedor.getStatus(),
                fornecedor.getCreatedAt(),
                fornecedor.getUpdatedAt()
        );
    }
}

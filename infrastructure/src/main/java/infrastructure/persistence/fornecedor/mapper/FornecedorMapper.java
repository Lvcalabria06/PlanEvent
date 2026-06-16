package infrastructure.persistence.fornecedor.mapper;

import domain.fornecedor.entity.Fornecedor;
import infrastructure.persistence.fornecedor.entity.FornecedorJpaEntity;

public final class FornecedorMapper {

    private FornecedorMapper() {}

    public static Fornecedor paraDominio(FornecedorJpaEntity entity) {
        return Fornecedor.reconstituir(
                entity.getId(),
                entity.getNome(),
                entity.getCnpj(),
                entity.getCategoriaServico(),
                entity.getContato(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static FornecedorJpaEntity paraJpa(Fornecedor fornecedor) {
        return new FornecedorJpaEntity(
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

package infrastructure.persistence.contrato.mapper;

import domain.contrato.entity.Contrato;
import domain.contrato.entity.ParteContrato;
import infrastructure.persistence.contrato.entity.ContratoJpaEntity;
import infrastructure.persistence.contrato.entity.ParteContratoJpaEntity;

import java.util.List;

public final class ContratoMapper {

    private ContratoMapper() {}

    public static Contrato paraDominio(ContratoJpaEntity entity) {
        List<ParteContrato> partes = entity.getPartes().stream()
                .map(p -> ParteContrato.reconstituir(p.getId(), entity.getId(), p.getNomeParte(), p.getTipoParte()))
                .toList();

        return Contrato.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getFornecedorId(),
                entity.getTipo(),
                entity.getObjeto(),
                entity.getValor(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                partes,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static ContratoJpaEntity paraJpa(Contrato contrato) {
        var entity = new ContratoJpaEntity(
                contrato.getId(),
                contrato.getEventoId(),
                contrato.getFornecedorId(),
                contrato.getTipo(),
                contrato.getObjeto(),
                contrato.getValor(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                contrato.getStatus(),
                contrato.getCreatedAt(),
                contrato.getUpdatedAt()
        );

        contrato.getPartes().forEach(p ->
                entity.getPartes().add(new ParteContratoJpaEntity(p.getId(), entity, p.getNomeParte(), p.getTipoParte()))
        );

        return entity;
    }
}

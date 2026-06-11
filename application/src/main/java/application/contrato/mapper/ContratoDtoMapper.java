package application.contrato.mapper;

import application.contrato.dto.ContratoResponse;
import application.contrato.dto.ParteContratoDto;
import domain.contrato.entity.Contrato;

public final class ContratoDtoMapper {

    private ContratoDtoMapper() {}

    public static ContratoResponse paraResposta(Contrato contrato) {
        var partes = contrato.getPartes().stream()
                .map(p -> new ParteContratoDto(p.getNomeParte(), p.getTipoParte()))
                .toList();

        return new ContratoResponse(
                contrato.getId(),
                contrato.getEventoId(),
                contrato.getFornecedorId(),
                contrato.getTipo(),
                contrato.getObjeto(),
                contrato.getValor(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                contrato.getStatus(),
                partes,
                contrato.getCreatedAt(),
                contrato.getUpdatedAt()
        );
    }
}

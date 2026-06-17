package infrastructure.persistence.conciliacao.mapper;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.valueobject.ItemRelatorioConciliacao;
import infrastructure.persistence.conciliacao.entity.ItemRelatorioJpaEntity;
import infrastructure.persistence.conciliacao.entity.RelatorioConciliacaoJpaEntity;

import java.util.UUID;

public final class RelatorioConciliacaoMapper {

    private RelatorioConciliacaoMapper() {}

    public static RelatorioConciliacao paraDominio(RelatorioConciliacaoJpaEntity entity) {
        var itens = entity.getItens().stream()
                .map(i -> new ItemRelatorioConciliacao(i.getDespesaId(), i.getContratoId(), i.getStatus(), i.getMetodo()))
                .toList();
        return RelatorioConciliacao.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getResponsavelId(),
                entity.getDataGeracao(),
                itens
        );
    }

    public static RelatorioConciliacaoJpaEntity paraJpa(RelatorioConciliacao relatorio) {
        var entity = new RelatorioConciliacaoJpaEntity(
                relatorio.getId(),
                relatorio.getEventoId(),
                relatorio.getResponsavelId(),
                relatorio.getDataGeracao()
        );
        relatorio.getItens().forEach(i ->
                entity.getItens().add(new ItemRelatorioJpaEntity(
                        UUID.randomUUID().toString(), entity,
                        i.despesaId(), i.contratoId(), i.status(), i.metodo()))
        );
        return entity;
    }
}

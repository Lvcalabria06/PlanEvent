package infrastructure.persistence.estoque.mapper;

import domain.estoque.entity.ItemPrevisao;
import domain.estoque.entity.ItemPrevisaoHistorico;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.entity.RegistroHistoricoPrevisao;
import infrastructure.persistence.estoque.entity.ItemPrevisaoHistoricoJpaEntity;
import infrastructure.persistence.estoque.entity.ItemPrevisaoJpaEntity;
import infrastructure.persistence.estoque.entity.PrevisaoConsumoJpaEntity;
import infrastructure.persistence.estoque.entity.RegistroHistoricoPrevisaoJpaEntity;

import java.util.List;

public final class PrevisaoConsumoMapper {

    private PrevisaoConsumoMapper() {
    }

    public static PrevisaoConsumoJpaEntity paraJpa(PrevisaoConsumo previsao) {
        PrevisaoConsumoJpaEntity entity = new PrevisaoConsumoJpaEntity(
                previsao.getId(),
                previsao.getEventoId(),
                previsao.getGeradoPorUsuarioId(),
                previsao.getDataGeracao(),
                previsao.getStatusHistorico(),
                previsao.isFallbackUtilizado(),
                previsao.isInvalidada(),
                previsao.getVersaoAtual(),
                previsao.getTotalEventosBase(),
                previsao.getTipoEventoReferencia(),
                previsao.getPorteEventoReferencia(),
                previsao.getDuracaoHorasReferencia());

        if (previsao.getItens() != null) {
            List<ItemPrevisaoJpaEntity> itens = previsao.getItens().stream()
                    .map(item -> new ItemPrevisaoJpaEntity(
                            item.getId(),
                            item.getItemEstoqueId(),
                            item.getCategoriaConsumo(),
                            item.getQuantidadeEstimada(),
                            item.getQuantidadeMinima(),
                            item.getQuantidadeMaxima(),
                            item.getQuantidadeFinal(),
                            item.getExplicacaoCalculo(),
                            entity))
                    .toList();
            entity.setItens(itens);
        }

        if (previsao.getHistoricoRegistros() != null) {
            List<RegistroHistoricoPrevisaoJpaEntity> historico = previsao.getHistoricoRegistros().stream()
                    .map(registro -> {
                        RegistroHistoricoPrevisaoJpaEntity registroEntity = new RegistroHistoricoPrevisaoJpaEntity(
                                registro.getId(),
                                registro.getVersao(),
                                registro.getTipoRegistro(),
                                registro.getUsuarioResponsavelId(),
                                registro.getDataHora(),
                                registro.getJustificativa(),
                                entity);
                        List<ItemPrevisaoHistoricoJpaEntity> itensHistorico = registro.getItens().stream()
                                .map(item -> new ItemPrevisaoHistoricoJpaEntity(
                                        item.getItemEstoqueId(),
                                        item.getCategoriaConsumo(),
                                        item.getQuantidadeEstimada(),
                                        item.getQuantidadeFinal()))
                                .toList();
                        registroEntity.setItens(itensHistorico);
                        return registroEntity;
                    })
                    .toList();
            entity.setHistoricoRegistros(historico);
        }

        return entity;
    }

    public static PrevisaoConsumo paraDominio(PrevisaoConsumoJpaEntity entity) {
        List<ItemPrevisao> itens = List.of();
        if (entity.getItens() != null) {
            itens = entity.getItens().stream()
                    .map(item -> ItemPrevisao.reconstituir(
                            item.getId(),
                            entity.getId(),
                            item.getItemEstoqueId(),
                            item.getCategoriaConsumo(),
                            item.getQuantidadeEstimada(),
                            item.getQuantidadeMinima(),
                            item.getQuantidadeMaxima(),
                            item.getExplicacaoCalculo(),
                            item.getQuantidadeFinal()))
                    .toList();
        }

        List<RegistroHistoricoPrevisao> historico = List.of();
        if (entity.getHistoricoRegistros() != null) {
            historico = entity.getHistoricoRegistros().stream()
                    .map(registro -> {
                        List<ItemPrevisaoHistorico> itensHistorico = registro.getItens().stream()
                                .map(item -> new ItemPrevisaoHistorico(
                                        item.getItemEstoqueId(),
                                        item.getCategoriaConsumo(),
                                        item.getQuantidadeEstimada(),
                                        item.getQuantidadeFinal()))
                                .toList();
                        return RegistroHistoricoPrevisao.reconstituir(
                                registro.getId(),
                                registro.getVersao(),
                                registro.getTipoRegistro(),
                                registro.getUsuarioResponsavelId(),
                                registro.getDataHora(),
                                registro.getJustificativa(),
                                itensHistorico);
                    })
                    .toList();
        }

        return PrevisaoConsumo.reconstituir(
                entity.getId(),
                entity.getEventoId(),
                entity.getGeradoPorUsuarioId(),
                entity.getDataGeracao(),
                entity.getStatusHistorico(),
                entity.isFallbackUtilizado(),
                entity.isInvalidada(),
                entity.getVersaoAtual(),
                entity.getTotalEventosBase(),
                entity.getTipoEventoReferencia(),
                entity.getPorteEventoReferencia(),
                entity.getDuracaoHorasReferencia(),
                itens,
                historico);
    }
}

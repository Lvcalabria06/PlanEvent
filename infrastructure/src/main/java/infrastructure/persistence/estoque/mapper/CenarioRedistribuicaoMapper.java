package infrastructure.persistence.estoque.mapper;

import domain.estoque.entity.AlocacaoRedistribuicao;
import domain.estoque.entity.CenarioRedistribuicao;
import infrastructure.persistence.estoque.entity.AlocacaoRedistribuicaoJpaEntity;
import infrastructure.persistence.estoque.entity.CenarioHistoricoJpaEntity;
import infrastructure.persistence.estoque.entity.CenarioRedistribuicaoJpaEntity;
import infrastructure.persistence.estoque.entity.TipoAlocacaoRedistribuicao;

import java.util.ArrayList;
import java.util.List;

public final class CenarioRedistribuicaoMapper {

    private CenarioRedistribuicaoMapper() {
    }

    public static CenarioRedistribuicaoJpaEntity paraJpa(CenarioRedistribuicao cenario) {
        CenarioRedistribuicaoJpaEntity entity = new CenarioRedistribuicaoJpaEntity(
                cenario.getId(),
                cenario.getDataCriacao(),
                cenario.getGeradoPorUsuarioId(),
                cenario.getPeriodoInicio(),
                cenario.getPeriodoFim(),
                cenario.getStatus(),
                cenario.getDataAplicacao(),
                cenario.getAplicadoPorUsuarioId());

        List<AlocacaoRedistribuicaoJpaEntity> alocacoes = new ArrayList<>();
        if (cenario.getAlocacoesAtuais() != null) {
            cenario.getAlocacoesAtuais().forEach(alocacao ->
                    alocacoes.add(paraAlocacaoJpa(alocacao, TipoAlocacaoRedistribuicao.ATUAL, entity, null)));
        }
        if (cenario.getAlocacoesOtimizadas() != null) {
            cenario.getAlocacoesOtimizadas().forEach(alocacao ->
                    alocacoes.add(paraAlocacaoJpa(alocacao, TipoAlocacaoRedistribuicao.OTIMIZADA, entity, null)));
        }
        entity.setAlocacoes(alocacoes);

        if (cenario.getHistorico() != null) {
            List<CenarioHistoricoJpaEntity> historicoEntities = cenario.getHistorico().stream()
                    .map(registro -> {
                        CenarioHistoricoJpaEntity historicoEntity = new CenarioHistoricoJpaEntity(
                                registro.getId(),
                                registro.getUsuarioResponsavelId(),
                                registro.getDataHora(),
                                registro.getDescricao(),
                                entity);
                        List<AlocacaoRedistribuicaoJpaEntity> snapshot = registro.getAlocacoesSnapshot().stream()
                                .map(alocacao -> paraAlocacaoJpa(
                                        alocacao,
                                        TipoAlocacaoRedistribuicao.OTIMIZADA,
                                        entity,
                                        historicoEntity))
                                .toList();
                        historicoEntity.setAlocacoesSnapshot(snapshot);
                        return historicoEntity;
                    })
                    .toList();
            entity.setHistorico(historicoEntities);
        }

        return entity;
    }

    public static CenarioRedistribuicao paraDominio(CenarioRedistribuicaoJpaEntity entity) {
        List<AlocacaoRedistribuicao> atuais = new ArrayList<>();
        List<AlocacaoRedistribuicao> otimizadas = new ArrayList<>();

        if (entity.getAlocacoes() != null) {
            for (AlocacaoRedistribuicaoJpaEntity alocacao : entity.getAlocacoes()) {
                if (alocacao.getHistorico() != null) {
                    continue;
                }
                AlocacaoRedistribuicao dominio = paraAlocacaoDominio(alocacao);
                if (alocacao.getTipoAlocacao() == TipoAlocacaoRedistribuicao.ATUAL) {
                    atuais.add(dominio);
                } else {
                    otimizadas.add(dominio);
                }
            }
        }

        List<CenarioRedistribuicao.RegistroHistorico> historico = List.of();
        if (entity.getHistorico() != null) {
            historico = entity.getHistorico().stream()
                    .map(registro -> {
                        List<AlocacaoRedistribuicao> snapshot = registro.getAlocacoesSnapshot().stream()
                                .map(CenarioRedistribuicaoMapper::paraAlocacaoDominio)
                                .toList();
                        return CenarioRedistribuicao.RegistroHistorico.reconstituir(
                                registro.getId(),
                                entity.getId(),
                                registro.getUsuarioResponsavelId(),
                                registro.getDataHora(),
                                registro.getDescricao(),
                                snapshot);
                    })
                    .toList();
        }

        return CenarioRedistribuicao.reconstituir(
                entity.getId(),
                entity.getDataCriacao(),
                entity.getGeradoPorUsuarioId(),
                entity.getPeriodoInicio(),
                entity.getPeriodoFim(),
                entity.getStatus(),
                atuais,
                otimizadas,
                historico,
                entity.getDataAplicacao(),
                entity.getAplicadoPorUsuarioId());
    }

    private static AlocacaoRedistribuicaoJpaEntity paraAlocacaoJpa(AlocacaoRedistribuicao alocacao,
                                                                   TipoAlocacaoRedistribuicao tipo,
                                                                   CenarioRedistribuicaoJpaEntity cenario,
                                                                   CenarioHistoricoJpaEntity historico) {
        return new AlocacaoRedistribuicaoJpaEntity(
                alocacao.getId(),
                alocacao.getEventoId(),
                alocacao.getItemEstoqueId(),
                alocacao.getQuantidadeAnterior(),
                alocacao.getQuantidadeRedistribuida(),
                alocacao.getItemSubstitutoId(),
                alocacao.getQuantidadeSubstituto(),
                tipo,
                cenario,
                historico);
    }

    private static AlocacaoRedistribuicao paraAlocacaoDominio(AlocacaoRedistribuicaoJpaEntity alocacao) {
        return AlocacaoRedistribuicao.reconstituir(
                alocacao.getId(),
                alocacao.getEventoId(),
                alocacao.getItemEstoqueId(),
                alocacao.getQuantidadeAnterior(),
                alocacao.getQuantidadeRedistribuida(),
                alocacao.getItemSubstitutoId(),
                alocacao.getQuantidadeSubstituto());
    }
}

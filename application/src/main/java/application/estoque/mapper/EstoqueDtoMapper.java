package application.estoque.mapper;

import application.estoque.dto.AlocacaoRedistribuicaoResponse;
import application.estoque.dto.CenarioRedistribuicaoResponse;
import application.estoque.dto.ConsumoEventoResponse;
import application.estoque.dto.ImpactoEventoResponse;
import application.estoque.dto.ImpactoItemResponse;
import application.estoque.dto.ItemConsumoResponse;
import application.estoque.dto.ItemEstoqueResponse;
import application.estoque.dto.ItemPrevisaoHistoricoResponse;
import application.estoque.dto.ItemPrevisaoResponse;
import application.estoque.dto.ItemReservaResponse;
import application.estoque.dto.ItemSubstituicaoResponse;
import application.estoque.dto.PrevisaoConsumoResponse;
import application.estoque.dto.RegistroHistoricoCenarioResponse;
import application.estoque.dto.RegistroHistoricoPrevisaoResponse;
import application.estoque.dto.ReservaEstoqueResponse;
import domain.estoque.entity.AlocacaoRedistribuicao;
import domain.estoque.entity.CenarioRedistribuicao;
import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemPrevisao;
import domain.estoque.entity.ItemPrevisaoHistorico;
import domain.estoque.entity.ItemReserva;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.entity.PrevisaoConsumo;
import domain.estoque.entity.RegistroHistoricoPrevisao;
import domain.estoque.entity.ReservaEstoque;

import java.util.List;

public final class EstoqueDtoMapper {

    private EstoqueDtoMapper() {
    }

    public static ItemEstoqueResponse paraResposta(ItemEstoque item) {
        return new ItemEstoqueResponse(
                item.getId(),
                item.getNome(),
                item.getQuantidadeTotal(),
                item.getQuantidadeDisponivel(),
                item.isAtivo(),
                item.getDataCriacao(),
                item.getDataAtualizacao());
    }

    public static ItemSubstituicaoResponse paraResposta(ItemSubstituicao substituicao) {
        return new ItemSubstituicaoResponse(
                substituicao.getId(),
                substituicao.getItemOriginalId(),
                substituicao.getItemSubstitutoId(),
                substituicao.getFatorEquivalencia());
    }

    public static ItemReservaResponse paraResposta(ItemReserva item) {
        return new ItemReservaResponse(item.getId(), item.getItemEstoqueId(), item.getQuantidade());
    }

    public static ReservaEstoqueResponse paraResposta(ReservaEstoque reserva) {
        List<ItemReservaResponse> itens = reserva.getItensReservados().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new ReservaEstoqueResponse(
                reserva.getId(),
                reserva.getEventoId(),
                reserva.getDataInicio(),
                reserva.getDataFim(),
                reserva.getStatus() != null ? reserva.getStatus().name() : null,
                itens);
    }

    public static ItemConsumoResponse paraResposta(ItemConsumoEvento item) {
        return new ItemConsumoResponse(
                item.getId(),
                item.getItemEstoqueId(),
                item.getCategoriaConsumo(),
                item.getQuantidadeConsumida());
    }

    public static ConsumoEventoResponse paraResposta(ConsumoEvento consumo) {
        List<ItemConsumoResponse> itens = consumo.getItensConsumidos().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new ConsumoEventoResponse(
                consumo.getId(),
                consumo.getEventoId(),
                consumo.getRegistradoPorUsuarioId(),
                consumo.getDataRegistro(),
                consumo.isValido(),
                itens);
    }

    public static ItemPrevisaoResponse paraResposta(ItemPrevisao item) {
        return new ItemPrevisaoResponse(
                item.getId(),
                item.getItemEstoqueId(),
                item.getCategoriaConsumo(),
                item.getQuantidadeEstimada(),
                item.getQuantidadeMinima(),
                item.getQuantidadeMaxima(),
                item.getQuantidadeFinal(),
                item.getExplicacaoCalculo());
    }

    public static ItemPrevisaoHistoricoResponse paraResposta(ItemPrevisaoHistorico item) {
        return new ItemPrevisaoHistoricoResponse(
                item.getItemEstoqueId(),
                item.getCategoriaConsumo(),
                item.getQuantidadeEstimada(),
                item.getQuantidadeFinal());
    }

    public static RegistroHistoricoPrevisaoResponse paraResposta(RegistroHistoricoPrevisao registro) {
        List<ItemPrevisaoHistoricoResponse> itens = registro.getItens().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new RegistroHistoricoPrevisaoResponse(
                registro.getId(),
                registro.getVersao(),
                registro.getTipoRegistro() != null ? registro.getTipoRegistro().name() : null,
                registro.getUsuarioResponsavelId(),
                registro.getDataHora(),
                registro.getJustificativa(),
                itens);
    }

    public static PrevisaoConsumoResponse paraResposta(PrevisaoConsumo previsao) {
        List<ItemPrevisaoResponse> itens = previsao.getItens().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        List<RegistroHistoricoPrevisaoResponse> historico = previsao.getHistoricoRegistros().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new PrevisaoConsumoResponse(
                previsao.getId(),
                previsao.getEventoId(),
                previsao.getGeradoPorUsuarioId(),
                previsao.getDataGeracao(),
                previsao.getStatusHistorico() != null ? previsao.getStatusHistorico().name() : null,
                previsao.isFallbackUtilizado(),
                previsao.isInvalidada(),
                previsao.getVersaoAtual(),
                previsao.getTotalEventosBase(),
                itens,
                historico);
    }

    public static AlocacaoRedistribuicaoResponse paraResposta(AlocacaoRedistribuicao alocacao) {
        return new AlocacaoRedistribuicaoResponse(
                alocacao.getId(),
                alocacao.getEventoId(),
                alocacao.getItemEstoqueId(),
                alocacao.getQuantidadeAnterior(),
                alocacao.getQuantidadeRedistribuida(),
                alocacao.getItemSubstitutoId(),
                alocacao.getQuantidadeSubstituto());
    }

    public static ImpactoItemResponse paraResposta(CenarioRedistribuicao.ImpactoEvento.ImpactoItem item) {
        return new ImpactoItemResponse(
                item.getItemEstoqueId(),
                item.getQuantidadeAnterior(),
                item.getQuantidadeRedistribuida(),
                item.getDeficit(),
                item.getExcesso());
    }

    public static ImpactoEventoResponse paraResposta(CenarioRedistribuicao.ImpactoEvento impacto) {
        List<ImpactoItemResponse> itens = impacto.getItensImpactados().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new ImpactoEventoResponse(impacto.getEventoId(), itens);
    }

    public static RegistroHistoricoCenarioResponse paraResposta(CenarioRedistribuicao.RegistroHistorico registro) {
        List<AlocacaoRedistribuicaoResponse> snapshot = registro.getAlocacoesSnapshot().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new RegistroHistoricoCenarioResponse(
                registro.getId(),
                registro.getUsuarioResponsavelId(),
                registro.getDataHora(),
                registro.getDescricao(),
                snapshot);
    }

    public static CenarioRedistribuicaoResponse paraResposta(CenarioRedistribuicao cenario) {
        List<AlocacaoRedistribuicaoResponse> atuais = cenario.getAlocacoesAtuais().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        List<AlocacaoRedistribuicaoResponse> otimizadas = cenario.getAlocacoesOtimizadas().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        List<ImpactoEventoResponse> impactos = cenario.getImpactosPorEvento().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        List<RegistroHistoricoCenarioResponse> historico = cenario.getHistorico().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
        return new CenarioRedistribuicaoResponse(
                cenario.getId(),
                cenario.getDataCriacao(),
                cenario.getGeradoPorUsuarioId(),
                cenario.getPeriodoInicio(),
                cenario.getPeriodoFim(),
                cenario.getStatus() != null ? cenario.getStatus().name() : null,
                atuais,
                otimizadas,
                impactos,
                historico,
                cenario.getDataAplicacao(),
                cenario.getAplicadoPorUsuarioId());
    }
}

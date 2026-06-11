package dev.proj.planevent.web;

import dev.proj.planevent.web.dto.FinanceiroDtos;
import domain.evento.entity.Evento;
import domain.financeiro.entity.Despesa;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.entity.SimulacaoRelatorioFinanceiro;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.DesvioOrcamentario;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.ResultadoGeracaoRelatorio;
import domain.fornecedor.entity.Fornecedor;

import java.util.List;

public final class FinanceiroMapper {

    private FinanceiroMapper() {
    }

    public static FinanceiroDtos.EventoResumoDto toEventoDto(Evento e) {
        return new FinanceiroDtos.EventoResumoDto(e.getId(), e.getNome(), e.isConcluido());
    }

    public static FinanceiroDtos.FornecedorResumoDto toFornecedorDto(Fornecedor f) {
        return new FinanceiroDtos.FornecedorResumoDto(f.getId(), f.getNome(), f.getCategoriaServico());
    }

    public static FinanceiroDtos.DespesaDto toDespesaDto(Despesa d) {
        return new FinanceiroDtos.DespesaDto(
                d.getId(),
                d.getEventoId(),
                d.getCategoria().name(),
                d.getFornecedorId(),
                d.getValor(),
                d.getData(),
                d.getLancadoPorUsuarioId(),
                d.getDataHoraLancamento(),
                d.getStatus().name(),
                d.getAprovadorId(),
                d.getMotivoRejeicao());
    }

    public static FinanceiroDtos.DesvioDto toDesvioDto(DesvioOrcamentario d) {
        return new FinanceiroDtos.DesvioDto(
                d.getCategoria().name(),
                d.getValorPrevisto(),
                d.getValorRealizado(),
                d.getDesvioPercentual(),
                d.getClassificacao().name());
    }

    public static FinanceiroDtos.RelatorioDto toRelatorioDto(RelatorioFinanceiro r) {
        return new FinanceiroDtos.RelatorioDto(
                r.getId(),
                r.getEventoId(),
                r.getGeradoPorUsuarioId(),
                r.getDataGeracao(),
                r.getTipo().name(),
                r.getMotivoNovaVersaoOficial(),
                r.getTotalGeralPrevisto(),
                r.getTotalGeralRealizado(),
                r.getItensPorCategoria().stream().map(FinanceiroMapper::toItem).toList(),
                new FinanceiroDtos.SaudeFinanceiraDto(
                        r.getSaudeFinanceira().getScore(),
                        r.getSaudeFinanceira().getClassificacao().name()),
                r.getComparativo() != null ? toComparativo(r.getComparativo()) : null,
                r.getCoberturaContratual() != null ? toCobertura(r.getCoberturaContratual()) : null,
                r.getRecomendacoes().stream().map(FinanceiroMapper::toRecomendacao).toList(),
                r.getConteudo());
    }

    public static FinanceiroDtos.RelatorioDto toRelatorioPreview(ResultadoGeracaoRelatorio r) {
        return new FinanceiroDtos.RelatorioDto(
                null,
                r.getEventoId(),
                r.getUsuarioId(),
                null,
                null,
                null,
                r.getTotalGeralPrevisto(),
                r.getTotalGeralRealizado(),
                r.getItens().stream().map(FinanceiroMapper::toItem).toList(),
                new FinanceiroDtos.SaudeFinanceiraDto(
                        r.getSaudeFinanceira().getScore(),
                        r.getSaudeFinanceira().getClassificacao().name()),
                r.getComparativo() != null ? toComparativo(r.getComparativo()) : null,
                r.getCoberturaContratual() != null ? toCobertura(r.getCoberturaContratual()) : null,
                r.getRecomendacoes().stream().map(FinanceiroMapper::toRecomendacao).toList(),
                r.getConteudo());
    }

    public static FinanceiroDtos.SimulacaoDto toSimulacaoDto(SimulacaoRelatorioFinanceiro s) {
        return new FinanceiroDtos.SimulacaoDto(
                s.getId(),
                toRelatorioPreview(s.getResultado()),
                s.getCriadaEm());
    }

    private static FinanceiroDtos.ItemRelatorioDto toItem(ItemRelatorioCategoria i) {
        return new FinanceiroDtos.ItemRelatorioDto(
                i.getCategoria().name(),
                i.getValorPrevisto(),
                i.getValorRealizado(),
                i.getPercentualVariacao(),
                i.getClassificacao().name());
    }

    private static FinanceiroDtos.ComparativoDto toComparativo(ComparativoRelatorioFinanceiro c) {
        List<String> cats = c.getCategoriasComPiora() != null
                ? c.getCategoriasComPiora().stream().map(Enum::name).toList()
                : List.of();
        return new FinanceiroDtos.ComparativoDto(
                c.getRelatorioAnteriorId(),
                c.getVariacaoScore(),
                c.getTendencia().name(),
                cats);
    }

    private static FinanceiroDtos.CoberturaContratualDto toCobertura(IndicadorCoberturaContratual c) {
        return new FinanceiroDtos.CoberturaContratualDto(
                c.getTotalDespesasAtivas(),
                c.getDespesasCobertas(),
                c.getDespesasDescobertas(),
                c.getPercentualCobertura());
    }

    private static FinanceiroDtos.RecomendacaoDto toRecomendacao(RecomendacaoFinanceira r) {
        return new FinanceiroDtos.RecomendacaoDto(
                r.getTipo().name(),
                r.getMensagem(),
                r.getCategoriaRelacionada() != null ? r.getCategoriaRelacionada().name() : null);
    }
}

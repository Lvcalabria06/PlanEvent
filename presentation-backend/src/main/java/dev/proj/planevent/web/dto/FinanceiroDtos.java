package dev.proj.planevent.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class FinanceiroDtos {

    private FinanceiroDtos() {
    }

    public record EventoResumoDto(String id, String nome, boolean concluido) {
    }

    public record FornecedorResumoDto(String id, String nome, String categoriaServico) {
    }

    public record DespesaDto(
            String id,
            String eventoId,
            String categoria,
            String fornecedorId,
            BigDecimal valor,
            LocalDateTime data,
            String lancadoPorUsuarioId,
            LocalDateTime dataHoraLancamento,
            String status,
            String aprovadorId,
            String motivoRejeicao) {
    }

    public record RegistrarDespesaRequest(
            String categoria,
            String fornecedorId,
            BigDecimal valor,
            LocalDateTime data) {
    }

    public record AtualizarDespesaRequest(BigDecimal valor, LocalDateTime data) {
    }

    public record DesvioDto(
            String categoria,
            BigDecimal valorPrevisto,
            BigDecimal valorRealizado,
            double desvioPercentual,
            String classificacao) {
    }

    public record AprovarDespesaRequest(String aprovadorId) {
    }

    public record RejeitarDespesaRequest(String aprovadorId, String motivo) {
    }

    public record ItemRelatorioDto(
            String categoria,
            BigDecimal valorPrevisto,
            BigDecimal valorRealizado,
            double percentualVariacao,
            String classificacao) {
    }

    public record SaudeFinanceiraDto(double score, String classificacao) {
    }

    public record ComparativoDto(
            String relatorioAnteriorId,
            double variacaoScore,
            String tendencia,
            List<String> categoriasComPiora) {
    }

    public record CoberturaContratualDto(
            int totalDespesasAtivas,
            int despesasCobertas,
            int despesasDescobertas,
            double percentualCobertura) {
    }

    public record RecomendacaoDto(String tipo, String mensagem, String categoriaRelacionada) {
    }

    public record RelatorioDto(
            String id,
            String eventoId,
            String geradoPorUsuarioId,
            LocalDateTime dataGeracao,
            String tipo,
            String motivoNovaVersaoOficial,
            BigDecimal totalGeralPrevisto,
            BigDecimal totalGeralRealizado,
            List<ItemRelatorioDto> itensPorCategoria,
            SaudeFinanceiraDto saudeFinanceira,
            ComparativoDto comparativo,
            CoberturaContratualDto coberturaContratual,
            List<RecomendacaoDto> recomendacoes,
            String conteudo) {
    }

    public record SimulacaoDto(String id, RelatorioDto preview, LocalDateTime criadaEm) {
    }

    public record ConfirmarRelatorioRequest(String tipo, String motivoNovaVersaoOficial) {
    }

    public record GerarOficialRequest(String motivoNovaVersaoOficial) {
    }
}

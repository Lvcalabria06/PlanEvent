package application.financeiro.dto;

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

    // --- Orçamento ---

    public record OrcamentoEventoDto(String id, String eventoId, BigDecimal valorTotal,
                                      LocalDateTime dataCriacao) {
    }

    public record CriarOrcamentoRequest(BigDecimal valorTotal) {
    }

    public record CategoriaOrcamentoDto(String id, String orcamentoId, String categoria,
                                         BigDecimal valorPrevisto) {
    }

    public record AdicionarCategoriaOrcamentoRequest(String categoria, BigDecimal valorPrevisto) {
    }

    public record AtualizarCategoriaOrcamentoRequest(BigDecimal valorPrevisto) {
    }

    // --- Ação Pós-Relatório (RN18) ---

    public record AcaoPosRelatorioDto(
            String id,
            String relatorioId,
            String tipoRecomendacao,
            String descricao,
            String status,
            LocalDateTime criadaEm,
            LocalDateTime tratadaEm) {
    }

    public record RegistrarAcaoPosRelatorioRequest(String tipoRecomendacao, String descricao) {
    }

    // --- Simulação What-If (RN15) ---

    public record DespesaHipoteticaRequest(String categoria, BigDecimal valor) {
    }

    public record SimularWhatIfRequest(
            boolean incluirPendentes,
            boolean cenarioPessimistaCobertura,
            List<DespesaHipoteticaRequest> despesasHipoteticas) {
    }

    // --- Comparativo entre dois snapshots (RN17) ---

    public record ComparativoRelatorioParDto(
            String relatorioBaseId,
            String relatorioComparadoId,
            double variacaoScore,
            BigDecimal variacaoTotalRealizado,
            String tendencia,
            List<String> categoriasComPiora,
            List<String> categoriasComMelhora) {
    }
}

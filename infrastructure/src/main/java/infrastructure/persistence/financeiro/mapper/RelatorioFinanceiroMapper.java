package infrastructure.persistence.financeiro.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.financeiro.entity.RelatorioFinanceiro;
import domain.financeiro.valueobject.ComparativoRelatorioFinanceiro;
import domain.financeiro.valueobject.IndicadorCoberturaContratual;
import domain.financeiro.valueobject.ItemRelatorioCategoria;
import domain.financeiro.valueobject.RecomendacaoFinanceira;
import domain.financeiro.valueobject.SaudeFinanceira;
import infrastructure.persistence.financeiro.entity.RelatorioFinanceiroJpaEntity;

import java.util.List;

public class RelatorioFinanceiroMapper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static RelatorioFinanceiroJpaEntity paraJpa(RelatorioFinanceiro dominio) {
        if (dominio == null) return null;
        try {
            return new RelatorioFinanceiroJpaEntity(
                    dominio.getId(),
                    dominio.getEventoId(),
                    dominio.getGeradoPorUsuarioId(),
                    dominio.getDataGeracao(),
                    dominio.getTipo(),
                    dominio.getMotivoNovaVersaoOficial(),
                    dominio.getTotalGeralPrevisto(),
                    dominio.getTotalGeralRealizado(),
                    mapper.writeValueAsString(dominio.getItensPorCategoria()),
                    mapper.writeValueAsString(dominio.getSaudeFinanceira()),
                    mapper.writeValueAsString(dominio.getCoberturaContratual()),
                    mapper.writeValueAsString(dominio.getComparativo()),
                    mapper.writeValueAsString(dominio.getRecomendacoes()),
                    dominio.getConteudo()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar atributos do Relatório Financeiro", e);
        }
    }

    public static RelatorioFinanceiro paraDominio(RelatorioFinanceiroJpaEntity jpa) {
        if (jpa == null) return null;
        try {
            List<ItemRelatorioCategoria> itens = new java.util.ArrayList<>();
            com.fasterxml.jackson.databind.JsonNode itensNode = mapper.readTree(jpa.getItensPorCategoriaJson());
            if (itensNode != null && itensNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode node : itensNode) {
                    itens.add(new ItemRelatorioCategoria(
                            domain.financeiro.valueobject.CategoriaDespesa.valueOf(node.get("categoria").asText()),
                            new java.math.BigDecimal(node.get("valorPrevisto").asText()),
                            new java.math.BigDecimal(node.get("valorRealizado").asText())
                    ));
                }
            }

            IndicadorCoberturaContratual cobertura = null;
            if (jpa.getCoberturaContratualJson() != null && !jpa.getCoberturaContratualJson().equals("null")) {
                com.fasterxml.jackson.databind.JsonNode cobNode = mapper.readTree(jpa.getCoberturaContratualJson());
                cobertura = new IndicadorCoberturaContratual(
                        cobNode.get("totalDespesasAtivas").asInt(),
                        cobNode.get("despesasCobertas").asInt(),
                        cobNode.get("despesasDescobertas").asInt()
                );
            }

            SaudeFinanceira saude = new SaudeFinanceira(itens, cobertura);

            ComparativoRelatorioFinanceiro comparativo = null;
            if (jpa.getComparativoJson() != null && !jpa.getComparativoJson().equals("null")) {
                com.fasterxml.jackson.databind.JsonNode compNode = mapper.readTree(jpa.getComparativoJson());
                java.util.List<domain.financeiro.valueobject.CategoriaDespesa> pioras = new java.util.ArrayList<>();
                if (compNode.has("categoriasComPiora") && compNode.get("categoriasComPiora").isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode pioraNode : compNode.get("categoriasComPiora")) {
                        pioras.add(domain.financeiro.valueobject.CategoriaDespesa.valueOf(pioraNode.asText()));
                    }
                }
                comparativo = new ComparativoRelatorioFinanceiro(
                        compNode.get("relatorioAnteriorId").asText(),
                        compNode.get("variacaoScore").asDouble(),
                        domain.financeiro.valueobject.TendenciaSaudeFinanceira.valueOf(compNode.get("tendencia").asText()),
                        pioras
                );
            }

            List<RecomendacaoFinanceira> recomendacoes = new java.util.ArrayList<>();
            if (jpa.getRecomendacoesJson() != null && !jpa.getRecomendacoesJson().equals("null")) {
                com.fasterxml.jackson.databind.JsonNode recNode = mapper.readTree(jpa.getRecomendacoesJson());
                if (recNode.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode node : recNode) {
                        domain.financeiro.valueobject.CategoriaDespesa cat = null;
                        if (node.has("categoriaRelacionada") && !node.get("categoriaRelacionada").isNull()) {
                            cat = domain.financeiro.valueobject.CategoriaDespesa.valueOf(node.get("categoriaRelacionada").asText());
                        }
                        recomendacoes.add(new RecomendacaoFinanceira(
                                domain.financeiro.valueobject.TipoRecomendacaoFinanceira.valueOf(node.get("tipo").asText()),
                                node.get("mensagem").asText(),
                                cat
                        ));
                    }
                }
            }

            return RelatorioFinanceiro.reconstruir(
                    jpa.getId(),
                    jpa.getEventoId(),
                    jpa.getGeradoPorUsuarioId(),
                    jpa.getDataGeracao(),
                    jpa.getTipo(),
                    jpa.getMotivoNovaVersaoOficial(),
                    jpa.getTotalGeralPrevisto(),
                    jpa.getTotalGeralRealizado(),
                    itens,
                    saude,
                    cobertura,
                    comparativo,
                    recomendacoes,
                    jpa.getConteudo()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao desserializar atributos do Relatório Financeiro", e);
        }
    }
}

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
            List<ItemRelatorioCategoria> itens = mapper.readValue(jpa.getItensPorCategoriaJson(), new TypeReference<List<ItemRelatorioCategoria>>() {});
            SaudeFinanceira saude = mapper.readValue(jpa.getSaudeFinanceiraJson(), SaudeFinanceira.class);
            
            IndicadorCoberturaContratual cobertura = null;
            if (jpa.getCoberturaContratualJson() != null && !jpa.getCoberturaContratualJson().equals("null")) {
                cobertura = mapper.readValue(jpa.getCoberturaContratualJson(), IndicadorCoberturaContratual.class);
            }
            
            ComparativoRelatorioFinanceiro comparativo = null;
            if (jpa.getComparativoJson() != null && !jpa.getComparativoJson().equals("null")) {
                comparativo = mapper.readValue(jpa.getComparativoJson(), ComparativoRelatorioFinanceiro.class);
            }
            
            List<RecomendacaoFinanceira> recomendacoes = List.of();
            if (jpa.getRecomendacoesJson() != null && !jpa.getRecomendacoesJson().equals("null")) {
                recomendacoes = mapper.readValue(jpa.getRecomendacoesJson(), new TypeReference<List<RecomendacaoFinanceira>>() {});
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

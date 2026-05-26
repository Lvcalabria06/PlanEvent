package domain.estoque.strategy;

import domain.estoque.entity.ItemPrevisao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstrategiaCalculoItemPrevisaoTest {

    private static final String PREVISAO_ID = "previsao-strategy-test";
    private static final String ITEM_ID = "agua";
    private static final String CATEGORIA = "bebida";

    @Test
    @DisplayName("MediaPonderada nao se aplica com menos de 2 historicos")
    void mediaPonderadaNaoSeAplicaComHistoricoEscasso() {
        EstrategiaCalculoItemPrevisao estrategia = new MediaPonderadaComHistoricoStrategy();

        ContextoCalculoItem contexto = new ContextoCalculoItem(
                PREVISAO_ID, ITEM_ID, CATEGORIA,
                List.of(novoHistorico("evt-1", 100.0)),
                List.of(novoHistorico("evt-1", 100.0))
        );

        assertFalse(estrategia.aplicavel(contexto));
    }

    @Test
    @DisplayName("MediaPonderada calcula previsao com pesos e remove outliers")
    void mediaPonderadaCalculaPrevisaoEDescriveAjustes() {
        EstrategiaCalculoItemPrevisao estrategia = new MediaPonderadaComHistoricoStrategy();

        List<RegistroHistoricoNormalizado> historicos = List.of(
                novoHistorico("evt-1", 200.0),
                novoHistorico("evt-2", 200.0),
                novoHistorico("evt-3", 220.0),
                novoHistorico("evt-4", 2000.0)
        );

        ContextoCalculoItem contexto = new ContextoCalculoItem(
                PREVISAO_ID, ITEM_ID, CATEGORIA, historicos, historicos);

        assertTrue(estrategia.aplicavel(contexto));

        ResultadoCalculoItem resultado = estrategia.calcular(contexto);
        ItemPrevisao item = resultado.getItemPrevisao();

        assertFalse(resultado.isFallbackAplicado());
        assertNotNull(item);
        assertTrue(item.getQuantidadeEstimada() < 500,
                "Outlier deve ser descartado, esperado < 500, obtido " + item.getQuantidadeEstimada());

        String explicacao = item.getExplicacaoCalculo();
        assertTrue(explicacao.contains("Eventos usados: evt-1, evt-2, evt-3"));
        assertTrue(explicacao.contains("Pesos aplicados"));
        assertTrue(explicacao.contains("Ajustes"));
    }

    @Test
    @DisplayName("Fallback sempre se aplica e marca fallback no resultado")
    void fallbackSempreAplicavelESinalizaFallback() {
        EstrategiaCalculoItemPrevisao estrategia = new FallbackParametrosPadraoStrategy();

        ContextoCalculoItem contextoSemBase = new ContextoCalculoItem(
                PREVISAO_ID, ITEM_ID, CATEGORIA, List.of(), List.of());

        assertTrue(estrategia.aplicavel(contextoSemBase));
        ResultadoCalculoItem resultado = estrategia.calcular(contextoSemBase);

        assertTrue(resultado.isFallbackAplicado());
        assertEquals(10, resultado.getItemPrevisao().getQuantidadeEstimada());
        assertTrue(resultado.getItemPrevisao().getExplicacaoCalculo().contains("Fallback aplicado"));
        assertTrue(resultado.getItemPrevisao().getExplicacaoCalculo().contains("parametro padrao"));
    }

    @Test
    @DisplayName("Fallback usa media global da categoria quando disponivel")
    void fallbackUsaMediaGlobalQuandoDisponivel() {
        EstrategiaCalculoItemPrevisao estrategia = new FallbackParametrosPadraoStrategy();

        ContextoCalculoItem contexto = new ContextoCalculoItem(
                PREVISAO_ID, ITEM_ID, CATEGORIA,
                List.of(novoHistorico("evt-1", 80.0)),
                List.of(
                        novoHistorico("evt-1", 80.0),
                        novoHistoricoCategoria("evt-2", "outra", 999.0)
                )
        );

        ResultadoCalculoItem resultado = estrategia.calcular(contexto);

        assertTrue(resultado.isFallbackAplicado());
        assertEquals(80, resultado.getItemPrevisao().getQuantidadeEstimada());
        assertTrue(resultado.getItemPrevisao().getExplicacaoCalculo().contains("media global da categoria"));
    }

    private RegistroHistoricoNormalizado novoHistorico(String eventoId, double quantidade) {
        return novoHistoricoCategoria(eventoId, CATEGORIA, quantidade);
    }

    private RegistroHistoricoNormalizado novoHistoricoCategoria(String eventoId, String categoria, double quantidade) {
        return new RegistroHistoricoNormalizado(
                eventoId, ITEM_ID, categoria, quantidade, 1.0, 0.5, 1.0);
    }
}

package domain.estoque.strategy;

import domain.estoque.entity.ItemPrevisao;

import java.util.List;

/**
 * Estrategia de fallback (RN6): usa media global por categoria quando nao ha
 * historico suficiente para o item; aplica valor padrao quando tambem nao ha
 * media global disponivel. Sempre marca {@code fallbackAplicado=true} para que
 * o agregado de previsao sinalize o uso de fallback.
 */
public class FallbackParametrosPadraoStrategy implements EstrategiaCalculoItemPrevisao {

    private static final int QUANTIDADE_PADRAO = 10;

    @Override
    public boolean aplicavel(ContextoCalculoItem contexto) {
        return true;
    }

    @Override
    public ResultadoCalculoItem calcular(ContextoCalculoItem contexto) {
        double mediaGlobal = calcularMediaGlobalPorCategoria(contexto);
        int estimada = (int) Math.round(mediaGlobal > 0 ? mediaGlobal : QUANTIDADE_PADRAO);

        String origem = mediaGlobal > 0
                ? "media global da categoria"
                : "parametro padrao (" + QUANTIDADE_PADRAO + ")";

        String explicacao = "Fallback aplicado para categoria " + contexto.getCategoria()
                + " por historico insuficiente. Pesos: nao se aplicam (sem base do item). "
                + "Ajustes: estimativa baseada em " + origem + ".";

        ItemPrevisao item = new ItemPrevisao(
                contexto.getPrevisaoId(),
                contexto.getItemId(),
                contexto.getCategoria(),
                estimada,
                Math.max(0, (int) Math.floor(estimada * 0.9)),
                (int) Math.ceil(estimada * 1.1),
                explicacao
        );

        return new ResultadoCalculoItem(item, true);
    }

    private double calcularMediaGlobalPorCategoria(ContextoCalculoItem contexto) {
        List<RegistroHistoricoNormalizado> globais = contexto.getHistoricosGlobais();
        return globais.stream()
                .filter(h -> h.getCategoria().equals(contexto.getCategoria()))
                .mapToDouble(RegistroHistoricoNormalizado::getQuantidadeNormalizada)
                .average()
                .orElse(0.0);
    }
}

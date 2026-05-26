package domain.estoque.strategy;

/**
 * Padrao Strategy aplicado ao calculo da previsao de consumo por item.
 * Cada estrategia decide se e aplicavel ao contexto e produz o
 * {@link ResultadoCalculoItem} correspondente, permitindo trocar livremente
 * entre algoritmos (media ponderada, fallback, modelos preditivos futuros)
 * sem alterar o servico que coordena a previsao.
 */
public interface EstrategiaCalculoItemPrevisao {

    boolean aplicavel(ContextoCalculoItem contexto);

    ResultadoCalculoItem calcular(ContextoCalculoItem contexto);
}

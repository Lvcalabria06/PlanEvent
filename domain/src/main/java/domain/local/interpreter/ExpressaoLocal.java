package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão base do padrão Interpreter (GoF) para filtros de {@link Local}.
 *
 * <p>Toda expressão — terminal ou composta — deve implementar esta interface
 * e retornar {@code true} quando o local avaliado satisfaz a condição.</p>
 */
public interface ExpressaoLocal {
    boolean interpretar(Local local);
}

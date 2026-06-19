package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão terminal que verifica se a capacidade do local é maior ou
 * igual ao valor mínimo esperado ({@code capacidade >= capacidadeMinima}).
 *
 * <p>Exemplo de uso na sintaxe do analisador: {@code capacidade_min = 100}</p>
 */
public class ExpressaoCapacidadeMinima implements ExpressaoLocal {

    private final int capacidadeMinima;

    public ExpressaoCapacidadeMinima(String valor) {
        try {
            this.capacidadeMinima = Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para 'capacidade_min': \"" + valor + "\". Informe um número inteiro.");
        }
        if (this.capacidadeMinima < 0) {
            throw new IllegalArgumentException("'capacidade_min' não pode ser negativo.");
        }
    }

    @Override
    public boolean interpretar(Local local) {
        return local.getCapacidade() >= capacidadeMinima;
    }
}

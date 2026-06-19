package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão terminal que verifica se a capacidade do local é menor ou
 * igual ao valor máximo esperado ({@code capacidade <= capacidadeMaxima}).
 *
 * <p>Exemplo de uso na sintaxe do analisador: {@code capacidade_max = 500}</p>
 */
public class ExpressaoCapacidadeMaxima implements ExpressaoLocal {

    private final int capacidadeMaxima;

    public ExpressaoCapacidadeMaxima(String valor) {
        try {
            this.capacidadeMaxima = Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Valor inválido para 'capacidade_max': \"" + valor + "\". Informe um número inteiro.");
        }
        if (this.capacidadeMaxima < 0) {
            throw new IllegalArgumentException("'capacidade_max' não pode ser negativo.");
        }
    }

    @Override
    public boolean interpretar(Local local) {
        return local.getCapacidade() <= capacidadeMaxima;
    }
}

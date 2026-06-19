package domain.local.interpreter;

import domain.local.entity.Local;

/**
 * Expressão terminal que verifica se o tipo do local é igual ao valor
 * esperado. A comparação é feita sem diferenciar maiúsculas de minúsculas
 * e desconsiderando espaços nas extremidades.
 *
 * <p>Exemplo de uso na sintaxe do analisador: {@code tipo = Salão}</p>
 */
public class ExpressaoTipo implements ExpressaoLocal {

    private final String tipoEsperado;

    public ExpressaoTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Valor do campo 'tipo' não pode ser vazio.");
        }
        this.tipoEsperado = tipo.trim().toLowerCase();
    }

    @Override
    public boolean interpretar(Local local) {
        return local.getTipo() != null
                && local.getTipo().trim().toLowerCase().equals(tipoEsperado);
    }
}

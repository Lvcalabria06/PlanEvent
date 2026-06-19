package domain.local.interpreter;

import domain.local.entity.Local;
import domain.local.valueobject.StatusLocal;

/**
 * Expressão terminal que verifica se o status do local é igual ao valor
 * esperado. Valores aceitos: {@code ATIVO}, {@code INATIVO},
 * {@code EM_MANUTENCAO} (insensível a maiúsculas/minúsculas).
 *
 * <p>Exemplo de uso na sintaxe do analisador: {@code status = ATIVO}</p>
 */
public class ExpressaoStatus implements ExpressaoLocal {

    private final StatusLocal statusEsperado;

    public ExpressaoStatus(String status) {
        try {
            this.statusEsperado = StatusLocal.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Status inválido: \"" + status + "\". Valores aceitos: ATIVO, INATIVO, EM_MANUTENCAO.");
        }
    }

    @Override
    public boolean interpretar(Local local) {
        return local.getStatus() == statusEsperado;
    }
}

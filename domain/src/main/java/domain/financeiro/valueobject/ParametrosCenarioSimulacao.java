package domain.financeiro.valueobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parâmetros de cenário what-if para simulação interativa (RN15 da Funcionalidade 2).
 * Toggles e despesas hipotéticas não alteram o ledger real de despesas.
 */
public class ParametrosCenarioSimulacao {

    private final boolean incluirPendentes;
    private final boolean cenarioPessimistaCobertura;
    private final List<DespesaHipotetica> despesasHipoteticas;

    public ParametrosCenarioSimulacao(boolean incluirPendentes,
                                       boolean cenarioPessimistaCobertura,
                                       List<DespesaHipotetica> despesasHipoteticas) {
        this.incluirPendentes = incluirPendentes;
        this.cenarioPessimistaCobertura = cenarioPessimistaCobertura;
        this.despesasHipoteticas = despesasHipoteticas != null
                ? Collections.unmodifiableList(new ArrayList<>(despesasHipoteticas))
                : List.of();
    }

    public static ParametrosCenarioSimulacao padrao() {
        return new ParametrosCenarioSimulacao(true, false, List.of());
    }

    public boolean isIncluirPendentes() { return incluirPendentes; }
    public boolean isCenarioPessimistaCobertura() { return cenarioPessimistaCobertura; }
    public List<DespesaHipotetica> getDespesasHipoteticas() { return despesasHipoteticas; }

    /**
     * Despesa hipotética para uso exclusivo em projeção what-if.
     */
    public record DespesaHipotetica(CategoriaDespesa categoria, BigDecimal valor) {
        public DespesaHipotetica {
            if (categoria == null) throw new IllegalArgumentException("Categoria é obrigatória.");
            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor hipotético deve ser maior que zero.");
            }
        }
    }
}

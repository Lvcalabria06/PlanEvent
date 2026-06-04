package domain.financeiro.entity;

import domain.financeiro.valueobject.ResultadoGeracaoRelatorio;

import java.time.LocalDateTime;
import java.util.UUID;

public class SimulacaoRelatorioFinanceiro {

    private final String id;
    private final ResultadoGeracaoRelatorio resultado;
    private final LocalDateTime criadaEm;

    public SimulacaoRelatorioFinanceiro(ResultadoGeracaoRelatorio resultado) {
        if (resultado == null) {
            throw new IllegalArgumentException("Resultado da simulação é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.resultado = resultado;
        this.criadaEm = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public ResultadoGeracaoRelatorio getResultado() {
        return resultado;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }
}

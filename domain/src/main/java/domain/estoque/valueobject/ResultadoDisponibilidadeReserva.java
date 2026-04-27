package domain.estoque.valueobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultadoDisponibilidadeReserva {
    private final boolean possuiConflito;
    private final List<DetalheConflitoReserva> conflitos;

    public ResultadoDisponibilidadeReserva(List<DetalheConflitoReserva> conflitos) {
        this.conflitos = Collections.unmodifiableList(new ArrayList<>(conflitos));
        this.possuiConflito = !conflitos.isEmpty();
    }

    public boolean isPossuiConflito() {
        return possuiConflito;
    }

    public List<DetalheConflitoReserva> getConflitos() {
        return conflitos;
    }
}

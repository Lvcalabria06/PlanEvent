package domain.estoque.valueobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalheConflitoReserva {
    private final String itemEstoqueId;
    private final int quantidadeSolicitada;
    private final int quantidadeDisponivelReal;
    private final int quantidadeJaReservada;
    private final List<String> eventosEmConflito;

    public DetalheConflitoReserva(String itemEstoqueId,
                                  int quantidadeSolicitada,
                                  int quantidadeDisponivelReal,
                                  int quantidadeJaReservada,
                                  List<String> eventosEmConflito) {
        this.itemEstoqueId = itemEstoqueId;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.quantidadeDisponivelReal = quantidadeDisponivelReal;
        this.quantidadeJaReservada = quantidadeJaReservada;
        this.eventosEmConflito = Collections.unmodifiableList(new ArrayList<>(eventosEmConflito));
    }

    public String getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public int getQuantidadeDisponivelReal() {
        return quantidadeDisponivelReal;
    }

    public int getQuantidadeJaReservada() {
        return quantidadeJaReservada;
    }

    public List<String> getEventosEmConflito() {
        return eventosEmConflito;
    }
}

package domain.estoque.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConsumoEvento {
    private final String id;
    private final String eventoId;
    private final String registradoPorUsuarioId;
    private final LocalDateTime dataRegistro;
    private final List<ItemConsumoEvento> itensConsumidos;
    private boolean valido;

    public ConsumoEvento(String eventoId, String registradoPorUsuarioId, List<ItemConsumoEvento> itensConsumidos) {
        if (eventoId == null || eventoId.isBlank()) {
            throw new IllegalArgumentException("ID do evento e obrigatorio.");
        }
        if (registradoPorUsuarioId == null || registradoPorUsuarioId.isBlank()) {
            throw new IllegalArgumentException("Usuario responsavel e obrigatorio.");
        }
        if (itensConsumidos == null || itensConsumidos.isEmpty()) {
            throw new IllegalArgumentException("O consumo deve possuir ao menos um item valido.");
        }
        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.registradoPorUsuarioId = registradoPorUsuarioId;
        this.dataRegistro = LocalDateTime.now();
        this.itensConsumidos = Collections.unmodifiableList(new ArrayList<>(itensConsumidos));
        this.valido = true;
    }

    public void invalidar() {
        this.valido = false;
    }

    public String getId() {
        return id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getRegistradoPorUsuarioId() {
        return registradoPorUsuarioId;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public List<ItemConsumoEvento> getItensConsumidos() {
        return itensConsumidos;
    }

    public boolean isValido() {
        return valido;
    }
}

package domain.local.entity;

import java.util.UUID;

public class AgendaLocal {
    private final String id;
    private final String localId;

    public AgendaLocal() {
        this.id = UUID.randomUUID().toString();
        this.localId = null;
    }

    public AgendaLocal(String localId) {
        if (localId == null) {
            throw new IllegalArgumentException("ID do local é obrigatório.");
        }
        this.id = UUID.randomUUID().toString();
        this.localId = localId;
    }

    // Getters
    public String getId() { return id; }
    public String getLocalId() { return localId; }
}

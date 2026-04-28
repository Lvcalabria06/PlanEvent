package domain.evento.planejamento;

import java.time.LocalDateTime;

public class TrocaLocalPlanejamento {

    private final LocalDateTime dataHora;
    private final String usuarioId;
    private final String motivo;
    private final String localAnteriorId;
    private final String localNovoId;

    public TrocaLocalPlanejamento(
            LocalDateTime dataHora,
            String usuarioId,
            String motivo,
            String localAnteriorId,
            String localNovoId) {
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
        this.localAnteriorId = localAnteriorId;
        this.localNovoId = localNovoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getLocalAnteriorId() {
        return localAnteriorId;
    }

    public String getLocalNovoId() {
        return localNovoId;
    }
}

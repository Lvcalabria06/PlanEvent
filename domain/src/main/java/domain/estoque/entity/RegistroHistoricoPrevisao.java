package domain.estoque.entity;

import domain.estoque.valueobject.TipoRegistroPrevisao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RegistroHistoricoPrevisao {
    private final String id;
    private final TipoRegistroPrevisao tipoRegistro;
    private final String usuarioResponsavelId;
    private final LocalDateTime dataHora;
    private final String observacao;
    private final List<ItemPrevisaoHistorico> itens;

    public RegistroHistoricoPrevisao(TipoRegistroPrevisao tipoRegistro,
                                     String usuarioResponsavelId,
                                     String observacao,
                                     List<ItemPrevisaoHistorico> itens) {
        if (tipoRegistro == null) {
            throw new IllegalArgumentException("Tipo de registro e obrigatorio.");
        }
        if (usuarioResponsavelId == null || usuarioResponsavelId.isBlank()) {
            throw new IllegalArgumentException("Usuario responsavel e obrigatorio.");
        }
        this.id = UUID.randomUUID().toString();
        this.tipoRegistro = tipoRegistro;
        this.usuarioResponsavelId = usuarioResponsavelId;
        this.dataHora = LocalDateTime.now();
        this.observacao = observacao;
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
    }

    public String getId() {
        return id;
    }

    public TipoRegistroPrevisao getTipoRegistro() {
        return tipoRegistro;
    }

    public String getUsuarioResponsavelId() {
        return usuarioResponsavelId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getObservacao() {
        return observacao;
    }

    public List<ItemPrevisaoHistorico> getItens() {
        return itens;
    }
}

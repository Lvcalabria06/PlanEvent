package domain.estoque.entity;

import domain.estoque.valueobject.TipoRegistroPrevisao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RegistroHistoricoPrevisao {
    private final String id;
    private final int versao;
    private final TipoRegistroPrevisao tipoRegistro;
    private final String usuarioResponsavelId;
    private final LocalDateTime dataHora;
    private final String justificativa;
    private final List<ItemPrevisaoHistorico> itens;

    public RegistroHistoricoPrevisao(int versao,
                                     TipoRegistroPrevisao tipoRegistro,
                                     String usuarioResponsavelId,
                                     String justificativa,
                                     List<ItemPrevisaoHistorico> itens) {
        this.id = UUID.randomUUID().toString();
        this.versao = versao;
        this.tipoRegistro = tipoRegistro;
        this.usuarioResponsavelId = usuarioResponsavelId;
        this.dataHora = LocalDateTime.now();
        this.justificativa = justificativa;
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
    }

    public String getId() {
        return id;
    }

    public int getVersao() {
        return versao;
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

    public String getJustificativa() {
        return justificativa;
    }

    public List<ItemPrevisaoHistorico> getItens() {
        return itens;
    }
}

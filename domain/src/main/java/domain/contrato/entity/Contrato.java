package domain.contrato.entity;

import domain.contrato.valueobject.DadosParteContrato;
import domain.contrato.valueobject.StatusContrato;
import domain.contrato.valueobject.TipoContrato;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Contrato {
    private final String id;
    private final String eventoId;
    private String fornecedorId;
    private TipoContrato tipo;
    private String objeto;
    private BigDecimal valor;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusContrato status;
    private final List<ParteContrato> partes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Contrato(String eventoId, TipoContrato tipo, String objeto, BigDecimal valor,
            LocalDateTime dataInicio, LocalDateTime dataFim, List<DadosParteContrato> dadosPartes) {

        validarCamposObrigatorios(eventoId, tipo, objeto, valor, dataInicio, dataFim, dadosPartes);

        this.id = UUID.randomUUID().toString();
        this.eventoId = eventoId;
        this.tipo = tipo;
        this.objeto = objeto.trim();
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusContrato.RASCUNHO;
        this.partes = new ArrayList<>();
        for (DadosParteContrato d : dadosPartes) {
            this.partes.add(new ParteContrato(this.id, d.nomeParte(), d.tipoParte()));
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void atualizarDetalhes(TipoContrato tipo, String objeto, BigDecimal valor,
            LocalDateTime dataInicio, LocalDateTime dataFim, List<DadosParteContrato> dadosPartes) {

        if (this.status == StatusContrato.ENCERRADO || this.status == StatusContrato.CANCELADO) {
            throw new IllegalStateException("Contrato não pode ser alterado neste estado.");
        }

        validarCamposObrigatorios(this.eventoId, tipo, objeto, valor, dataInicio, dataFim, dadosPartes);

        this.tipo = tipo;
        this.objeto = objeto.trim();
        this.valor = valor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.partes.clear();
        for (DadosParteContrato d : dadosPartes) {
            this.partes.add(new ParteContrato(this.id, d.nomeParte(), d.tipoParte()));
        }
        this.updatedAt = LocalDateTime.now();
    }

    public boolean estaCompleto() {
        try {
            validarCamposObrigatorios(eventoId, tipo, objeto, valor, dataInicio, dataFim, getDadosPartesInterno());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void encerrar() {
        if (this.status == StatusContrato.CANCELADO) {
            throw new IllegalStateException("Contrato cancelado não pode ser encerrado.");
        }

        if (this.status == StatusContrato.ENCERRADO) {
            throw new IllegalStateException("Contrato já está encerrado.");
        }

        if (!estaCompleto()) {
            throw new IllegalStateException(
                    "Não é permitido encerrar contrato com informações incompletas ou inconsistentes.");
        }

        this.status = StatusContrato.ENCERRADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void assinar() {
        if (this.status != StatusContrato.RASCUNHO && this.status != StatusContrato.EM_NEGOCIACAO) {
            throw new IllegalStateException("Contrato não está em um estado válido para assinatura.");
        }

        this.status = StatusContrato.ASSINADO;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancelar() {
        if (this.status == StatusContrato.ENCERRADO) {
            throw new IllegalStateException("Um contrato encerrado não pode ser cancelado.");
        }

        this.status = StatusContrato.CANCELADO;
        this.updatedAt = LocalDateTime.now();
    }

    private List<DadosParteContrato> getDadosPartesInterno() {
        List<DadosParteContrato> list = new ArrayList<>();
        for (ParteContrato p : partes) {
            list.add(new DadosParteContrato(p.getNomeParte(), p.getTipoParte()));
        }
        return list;
    }

    private static void validarCamposObrigatorios(String eventoId, TipoContrato tipo, String objeto,
            BigDecimal valor, LocalDateTime dataInicio, LocalDateTime dataFim,
            List<DadosParteContrato> dadosPartes) {
        if (eventoId == null || eventoId.trim().isEmpty()) {
            throw new IllegalArgumentException("Evento do contrato é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de contrato é obrigatório.");
        }
        if (objeto == null || objeto.trim().isEmpty()) {
            throw new IllegalArgumentException("Objeto do contrato é obrigatório.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do contrato é obrigatório e deve ser maior que zero.");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas de vigência do contrato são obrigatórias.");
        }
        if (!dataInicio.isBefore(dataFim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de término.");
        }
        if (dadosPartes == null || dadosPartes.size() < 2) {
            throw new IllegalArgumentException("É necessário informar ao menos duas partes no contrato.");
        }
    }

    public void definirFornecedor(String fornecedorId) {
        this.fornecedorId = fornecedorId;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getEventoId() { return eventoId; }
    public String getFornecedorId() { return fornecedorId; }
    public TipoContrato getTipo() { return tipo; }
    public String getObjeto() { return objeto; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public StatusContrato getStatus() { return status; }
    public List<ParteContrato> getPartes() { return Collections.unmodifiableList(partes); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

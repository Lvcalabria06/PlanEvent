package domain.contrato.service;

import domain.contrato.entity.Contrato;

import java.util.List;

public interface ContratoService {

    Contrato criarContrato(Contrato contrato);

    Contrato editarContrato(Contrato contrato);

    Contrato buscarContrato(String id);

    List<Contrato> listarContratosPorEvento(String eventoId);

    void encerrarContrato(String id);
}

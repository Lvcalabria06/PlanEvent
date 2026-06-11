package application.contrato.usecase;

import application.contrato.dto.ContratoResponse;
import application.contrato.dto.CriarContratoRequest;
import application.contrato.dto.EditarContratoRequest;
import application.contrato.mapper.ContratoDtoMapper;
import domain.contrato.entity.Contrato;
import domain.contrato.service.ContratoService;
import domain.contrato.valueobject.DadosParteContrato;

import java.util.List;

public class ContratoUseCaseImpl implements ContratoUseCase {

    private final ContratoService contratoService;

    public ContratoUseCaseImpl(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @Override
    public ContratoResponse criar(CriarContratoRequest request) {
        var dadosPartes = request.partes().stream()
                .map(p -> new DadosParteContrato(p.nomeParte(), p.tipoParte()))
                .toList();

        var novoContrato = new Contrato(
                request.eventoId(),
                request.fornecedorId(),
                request.tipo(),
                request.objeto(),
                request.valor(),
                request.dataInicio(),
                request.dataFim(),
                dadosPartes);

        return ContratoDtoMapper.paraResposta(contratoService.criarContrato(novoContrato));
    }

    @Override
    public ContratoResponse editar(String id, EditarContratoRequest request) {
        Contrato atual = contratoService.buscarContrato(id);

        var dadosPartes = request.partes().stream()
                .map(p -> new DadosParteContrato(p.nomeParte(), p.tipoParte()))
                .toList();

        atual.atualizarDetalhes(
                request.tipo(),
                request.objeto(),
                request.valor(),
                request.dataInicio(),
                request.dataFim(),
                dadosPartes);

        return ContratoDtoMapper.paraResposta(contratoService.editarContrato(atual));
    }

    @Override
    public ContratoResponse buscar(String id) {
        return ContratoDtoMapper.paraResposta(contratoService.buscarContrato(id));
    }

    @Override
    public List<ContratoResponse> listar() {
        return contratoService.listarTodosContratos().stream()
                .map(ContratoDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<ContratoResponse> listarPorEvento(String eventoId) {
        return contratoService.listarContratosPorEvento(eventoId).stream()
                .map(ContratoDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public ContratoResponse encerrar(String id) {
        contratoService.encerrarContrato(id);
        return ContratoDtoMapper.paraResposta(contratoService.buscarContrato(id));
    }
}

package application.fornecedor.usecase;

import application.fornecedor.dto.CadastrarFornecedorRequest;
import application.fornecedor.dto.EditarFornecedorRequest;
import application.fornecedor.dto.FornecedorResponse;
import application.fornecedor.mapper.FornecedorDtoMapper;
import domain.fornecedor.service.FornecedorService;

import java.util.List;

public class FornecedorUseCaseImpl implements FornecedorUseCase {

    private final FornecedorService fornecedorService;

    public FornecedorUseCaseImpl(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @Override
    public FornecedorResponse cadastrar(CadastrarFornecedorRequest request) {
        return FornecedorDtoMapper.paraResposta(
                fornecedorService.cadastrarFornecedor(
                        request.nome(),
                        request.cnpj(),
                        request.categoriaServico(),
                        request.contato()));
    }

    @Override
    public FornecedorResponse editar(String id, EditarFornecedorRequest request) {
        return FornecedorDtoMapper.paraResposta(
                fornecedorService.editarFornecedor(
                        id,
                        request.nome(),
                        request.cnpj(),
                        request.categoriaServico(),
                        request.contato()));
    }

    @Override
    public FornecedorResponse buscar(String id) {
        return FornecedorDtoMapper.paraResposta(fornecedorService.buscarFornecedor(id));
    }

    @Override
    public List<FornecedorResponse> listar() {
        return fornecedorService.listarFornecedores().stream()
                .map(FornecedorDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public void desativar(String id) {
        fornecedorService.desativarFornecedor(id);
    }
}

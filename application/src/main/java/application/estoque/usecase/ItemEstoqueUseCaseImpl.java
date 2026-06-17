package application.estoque.usecase;

import application.estoque.dto.AdicionarEstoqueRequest;
import application.estoque.dto.CadastrarItemEstoqueRequest;
import application.estoque.dto.CadastrarSubstituicaoRequest;
import application.estoque.dto.EditarItemEstoqueRequest;
import application.estoque.dto.ItemEstoqueResponse;
import application.estoque.dto.ItemSubstituicaoResponse;
import application.estoque.mapper.EstoqueDtoMapper;
import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.service.ItemEstoqueService;

import java.util.List;

public class ItemEstoqueUseCaseImpl implements ItemEstoqueUseCase {

    private final ItemEstoqueService itemEstoqueService;

    public ItemEstoqueUseCaseImpl(ItemEstoqueService itemEstoqueService) {
        this.itemEstoqueService = itemEstoqueService;
    }

    @Override
    public ItemEstoqueResponse cadastrar(CadastrarItemEstoqueRequest request) {
        ItemEstoque item = itemEstoqueService.cadastrar(request.nome(), request.quantidadeTotal());
        return EstoqueDtoMapper.paraResposta(item);
    }

    @Override
    public ItemEstoqueResponse editar(String id, EditarItemEstoqueRequest request) {
        ItemEstoque item = itemEstoqueService.editar(id, request.nome(), request.quantidadeTotal());
        return EstoqueDtoMapper.paraResposta(item);
    }

    @Override
    public void desativar(String id) {
        itemEstoqueService.desativar(id);
    }

    @Override
    public void reativar(String id) {
        itemEstoqueService.reativar(id);
    }

    @Override
    public ItemEstoqueResponse adicionarEstoque(String id, AdicionarEstoqueRequest request) {
        ItemEstoque item = itemEstoqueService.adicionarEstoque(id, request.quantidade());
        return EstoqueDtoMapper.paraResposta(item);
    }

    @Override
    public ItemEstoqueResponse buscar(String id) {
        ItemEstoque item = itemEstoqueService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Item de estoque nao encontrado."));
        return EstoqueDtoMapper.paraResposta(item);
    }

    @Override
    public List<ItemEstoqueResponse> listarTodos() {
        return itemEstoqueService.listarTodos().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<ItemEstoqueResponse> listarAtivos() {
        return itemEstoqueService.listarAtivos().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public ItemSubstituicaoResponse cadastrarSubstituicao(CadastrarSubstituicaoRequest request) {
        ItemSubstituicao sub = itemEstoqueService.registrarSubstituicao(
                request.itemOriginalId(),
                request.itemSubstitutoId(),
                request.fatorEquivalencia());
        return EstoqueDtoMapper.paraResposta(sub);
    }

    @Override
    public List<ItemSubstituicaoResponse> listarSubstituicoes() {
        return itemEstoqueService.listarSubstituicoes().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }
}

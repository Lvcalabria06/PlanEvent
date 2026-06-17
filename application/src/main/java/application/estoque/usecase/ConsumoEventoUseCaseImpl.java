package application.estoque.usecase;

import application.estoque.dto.ConsumoEventoResponse;
import application.estoque.dto.ItemConsumoRequest;
import application.estoque.dto.RegistrarConsumoEventoRequest;
import application.estoque.mapper.EstoqueDtoMapper;
import domain.estoque.entity.ConsumoEvento;
import domain.estoque.entity.ItemConsumoEvento;
import domain.estoque.service.ConsumoEventoService;

import java.util.List;

public class ConsumoEventoUseCaseImpl implements ConsumoEventoUseCase {

    private final ConsumoEventoService consumoEventoService;

    public ConsumoEventoUseCaseImpl(ConsumoEventoService consumoEventoService) {
        this.consumoEventoService = consumoEventoService;
    }

    @Override
    public ConsumoEventoResponse registrar(RegistrarConsumoEventoRequest request) {
        List<ItemConsumoEvento> itens = request.itens().stream()
                .map(this::paraItemConsumo)
                .toList();
        ConsumoEvento consumo = consumoEventoService.registrar(
                request.eventoId(),
                request.usuarioId(),
                itens);
        return EstoqueDtoMapper.paraResposta(consumo);
    }

    @Override
    public void invalidar(String id) {
        consumoEventoService.invalidar(id);
    }

    @Override
    public ConsumoEventoResponse buscar(String id) {
        ConsumoEvento consumo = consumoEventoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de consumo nao encontrado."));
        return EstoqueDtoMapper.paraResposta(consumo);
    }

    @Override
    public List<ConsumoEventoResponse> listarPorEvento(String eventoId) {
        return consumoEventoService.listarPorEvento(eventoId).stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    @Override
    public List<ConsumoEventoResponse> listarTodos() {
        return consumoEventoService.listarTodos().stream()
                .map(EstoqueDtoMapper::paraResposta)
                .toList();
    }

    private ItemConsumoEvento paraItemConsumo(ItemConsumoRequest request) {
        return new ItemConsumoEvento(
                request.itemEstoqueId(),
                request.categoriaConsumo(),
                request.quantidadeConsumida());
    }
}

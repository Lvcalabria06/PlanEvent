package domain.estoque.support;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.repository.ItemEstoqueRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryItemEstoqueRepository implements ItemEstoqueRepository {

    private final Map<String, ItemEstoque> porId = new ConcurrentHashMap<>();
    private final List<ItemSubstituicao> substituicoes = new ArrayList<>();

    @Override
    public synchronized ItemEstoque salvar(ItemEstoque itemEstoque) {
        porId.put(itemEstoque.getId(), itemEstoque);
        return itemEstoque;
    }

    @Override
    public Optional<ItemEstoque> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<ItemEstoque> listarTodos() {
        return new ArrayList<>(porId.values());
    }

    @Override
    public List<ItemEstoque> listarAtivos() {
        return porId.values().stream()
                .filter(ItemEstoque::isAtivo)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemSubstituicao> buscarSubstituicoesPorItem(String itemOriginalId) {
        return substituicoes.stream()
                .filter(s -> s.getItemOriginalId().equals(itemOriginalId))
                .collect(Collectors.toList());
    }

    public void registrarSubstituicao(ItemSubstituicao substituicao) {
        substituicoes.add(substituicao);
    }

    public void limpar() {
        porId.clear();
        substituicoes.clear();
    }
}

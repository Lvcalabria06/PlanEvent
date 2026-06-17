package infrastructure.persistence.estoque.adapter;

import domain.estoque.entity.ItemEstoque;
import domain.estoque.entity.ItemSubstituicao;
import domain.estoque.repository.ItemEstoqueRepository;
import infrastructure.persistence.estoque.entity.ItemSubstituicaoJpaEntity;
import infrastructure.persistence.estoque.mapper.ItemEstoqueMapper;
import infrastructure.persistence.estoque.repository.ItemEstoqueJpaRepository;
import infrastructure.persistence.estoque.repository.ItemSubstituicaoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemEstoqueRepositoryJpaAdapter implements ItemEstoqueRepository {

    private final ItemEstoqueJpaRepository itemEstoqueJpaRepository;
    private final ItemSubstituicaoJpaRepository itemSubstituicaoJpaRepository;

    public ItemEstoqueRepositoryJpaAdapter(ItemEstoqueJpaRepository itemEstoqueJpaRepository,
                                           ItemSubstituicaoJpaRepository itemSubstituicaoJpaRepository) {
        this.itemEstoqueJpaRepository = itemEstoqueJpaRepository;
        this.itemSubstituicaoJpaRepository = itemSubstituicaoJpaRepository;
    }

    @Override
    public ItemEstoque salvar(ItemEstoque itemEstoque) {
        return ItemEstoqueMapper.paraDominio(itemEstoqueJpaRepository.save(ItemEstoqueMapper.paraJpa(itemEstoque)));
    }

    @Override
    public Optional<ItemEstoque> buscarPorId(String id) {
        return itemEstoqueJpaRepository.findById(id).map(ItemEstoqueMapper::paraDominio);
    }

    @Override
    public List<ItemEstoque> listarTodos() {
        return itemEstoqueJpaRepository.findAll().stream()
                .map(ItemEstoqueMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ItemEstoque> listarAtivos() {
        return itemEstoqueJpaRepository.findByAtivoTrue().stream()
                .map(ItemEstoqueMapper::paraDominio)
                .toList();
    }

    @Override
    public List<ItemSubstituicao> buscarSubstituicoesPorItem(String itemOriginalId) {
        return itemSubstituicaoJpaRepository.findByItemOriginalId(itemOriginalId).stream()
                .map(entity -> ItemSubstituicao.reconstituir(
                        entity.getId(),
                        entity.getItemOriginalId(),
                        entity.getItemSubstitutoId(),
                        entity.getFatorEquivalencia()))
                .toList();
    }

    @Override
    public List<ItemSubstituicao> listarSubstituicoes() {
        return itemSubstituicaoJpaRepository.findAll().stream()
                .map(entity -> ItemSubstituicao.reconstituir(
                        entity.getId(),
                        entity.getItemOriginalId(),
                        entity.getItemSubstitutoId(),
                        entity.getFatorEquivalencia()))
                .toList();
    }

    @Override
    public ItemSubstituicao salvarSubstituicao(ItemSubstituicao substituicao) {
        var entity = new ItemSubstituicaoJpaEntity(
                substituicao.getId(),
                substituicao.getItemOriginalId(),
                substituicao.getItemSubstitutoId(),
                substituicao.getFatorEquivalencia());
        var saved = itemSubstituicaoJpaRepository.save(entity);
        return ItemSubstituicao.reconstituir(
                saved.getId(),
                saved.getItemOriginalId(),
                saved.getItemSubstitutoId(),
                saved.getFatorEquivalencia());
    }
}

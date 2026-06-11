package infrastructure.persistence.memory;

import domain.financeiro.entity.CategoriaOrcamento;
import domain.financeiro.repository.CategoriaOrcamentoRepository;
import domain.financeiro.valueobject.CategoriaDespesa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryCategoriaOrcamentoRepository implements CategoriaOrcamentoRepository {

    private final Map<String, CategoriaOrcamento> porId = new ConcurrentHashMap<>();

    @Override
    public CategoriaOrcamento salvar(CategoriaOrcamento categoria) {
        porId.put(categoria.getId(), categoria);
        return categoria;
    }

    @Override
    public Optional<CategoriaOrcamento> buscarPorOrcamentoECategoria(String orcamentoId,
                                                                      CategoriaDespesa categoria) {
        return porId.values().stream()
                .filter(c -> orcamentoId.equals(c.getOrcamentoId()) && c.getNome() == categoria)
                .findFirst();
    }

    @Override
    public List<CategoriaOrcamento> listarPorOrcamentoId(String orcamentoId) {
        return porId.values().stream()
                .filter(c -> orcamentoId.equals(c.getOrcamentoId()))
                .toList();
    }

    public void limpar() {
        porId.clear();
    }
}

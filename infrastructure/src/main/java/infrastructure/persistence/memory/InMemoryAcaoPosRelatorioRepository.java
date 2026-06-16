package infrastructure.persistence.memory;

import domain.financeiro.entity.AcaoPosRelatorio;
import domain.financeiro.repository.AcaoPosRelatorioRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryAcaoPosRelatorioRepository implements AcaoPosRelatorioRepository {

    private final Map<String, AcaoPosRelatorio> porId = new ConcurrentHashMap<>();

    @Override
    public AcaoPosRelatorio salvar(AcaoPosRelatorio acao) {
        porId.put(acao.getId(), acao);
        return acao;
    }

    @Override
    public Optional<AcaoPosRelatorio> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<AcaoPosRelatorio> listarPorRelatorioId(String relatorioId) {
        return porId.values().stream()
                .filter(a -> relatorioId.equals(a.getRelatorioId()))
                .toList();
    }

    public void limpar() {
        porId.clear();
    }
}

package infrastructure.persistence.memory;

import domain.financeiro.entity.OrcamentoEvento;
import domain.financeiro.repository.OrcamentoEventoRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Primary
public class InMemoryOrcamentoEventoRepository implements OrcamentoEventoRepository {

    private final Map<String, OrcamentoEvento> porId = new ConcurrentHashMap<>();
    private final Map<String, String> eventoParaOrcamentoId = new ConcurrentHashMap<>();

    @Override
    public OrcamentoEvento salvar(OrcamentoEvento orcamento) {
        porId.put(orcamento.getId(), orcamento);
        eventoParaOrcamentoId.put(orcamento.getEventoId(), orcamento.getId());
        return orcamento;
    }

    @Override
    public Optional<OrcamentoEvento> buscarPorId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public Optional<OrcamentoEvento> buscarPorEventoId(String eventoId) {
        String orcamentoId = eventoParaOrcamentoId.get(eventoId);
        if (orcamentoId == null) {
            return Optional.empty();
        }
        return buscarPorId(orcamentoId);
    }

    public void limpar() {
        porId.clear();
        eventoParaOrcamentoId.clear();
    }
}

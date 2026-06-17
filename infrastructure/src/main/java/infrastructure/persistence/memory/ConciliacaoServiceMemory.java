package infrastructure.persistence.memory;

import domain.conciliacao.entity.RelatorioConciliacao;
import domain.conciliacao.entity.VinculoConciliacao;
import domain.conciliacao.service.ConciliacaoService;
import domain.contrato.entity.Contrato;
import domain.financeiro.entity.Despesa;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConciliacaoServiceMemory implements ConciliacaoService {

    private final InMemoryDespesaRepository despesaRepository;
    private final Map<String, List<String>> despesasDescobertasPorEvento = new ConcurrentHashMap<>();

    public ConciliacaoServiceMemory(InMemoryDespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    public void registrarDespesasDescobertas(String eventoId, List<String> despesaIds) {
        despesasDescobertasPorEvento.put(eventoId, new ArrayList<>(despesaIds));
    }

    @Override
    public void executarConciliacaoAutomatica(String eventoId, String responsavelId) {
        // no-op para protótipo em memória
    }

    @Override
    public List<Despesa> listarDespesasDescobertasPorEvento(String eventoId) {
        List<String> ids = despesasDescobertasPorEvento.getOrDefault(eventoId, List.of());
        return ids.stream()
                .map(despesaRepository::buscarPorId)
                .flatMap(java.util.Optional::stream)
                .toList();
    }

    @Override
    public List<Contrato> listarContratosExtrapoladosPorEvento(String eventoId) {
        return List.of();
    }

    @Override
    public VinculoConciliacao vincularManualmente(String despesaId, String contratoId, String responsavelId) {
        throw new UnsupportedOperationException("Conciliação manual não disponível no modo memória.");
    }

    @Override
    public List<VinculoConciliacao> listarVinculosPorEvento(String eventoId) {
        return List.of();
    }

    @Override
    public RelatorioConciliacao gerarRelatorio(String eventoId, String responsavelId) {
        throw new UnsupportedOperationException("Relatório de conciliação não disponível no modo memória.");
    }
}

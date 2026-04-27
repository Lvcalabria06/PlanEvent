package domain.local.service;

import domain.local.entity.Local;
import domain.local.entity.ManutencaoLocal;
import domain.local.entity.ReservaLocal;
import domain.local.repository.LocalRepository;
import domain.local.repository.ManutencaoRepository;
import domain.local.repository.ReservaLocalRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ManutencaoServiceImpl implements ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final LocalRepository localRepository;
    private final ReservaLocalRepository reservaLocalRepository;

    public ManutencaoServiceImpl(ManutencaoRepository manutencaoRepository,
                                 LocalRepository localRepository,
                                 ReservaLocalRepository reservaLocalRepository) {
        this.manutencaoRepository = manutencaoRepository;
        this.localRepository = localRepository;
        this.reservaLocalRepository = reservaLocalRepository;
    }

    @Override
    public ManutencaoLocal cadastrarManutencao(String localId, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel) {
        // RN3: A manutenção deve estar vinculada a um local existente
        Local local = localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));

        // Cria a entidade (já aplica validações de RN1, RN2, RN8, RN9)
        ManutencaoLocal manutencao = new ManutencaoLocal(localId, dataInicio, dataFim, responsavel);

        // RN5: Validar conflito de reservas
        validarConflitoComReservas(localId, dataInicio, dataFim);

        return manutencaoRepository.salvar(manutencao);
    }

    @Override
    public ManutencaoLocal editarManutencao(String id, LocalDateTime dataInicio, LocalDateTime dataFim, String responsavel) {
        ManutencaoLocal manutencao = manutencaoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada."));

        // RN6: Validação sobre reservas futuras conflitantes na edição
        validarConflitoComReservas(manutencao.getLocalId(), dataInicio, dataFim);

        manutencao.atualizar(dataInicio, dataFim, responsavel);
        return manutencaoRepository.salvar(manutencao);
    }

    @Override
    public void removerManutencao(String id) {
        ManutencaoLocal manutencao = manutencaoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Manutenção não encontrada."));

        // RN10: A remoção de uma manutenção deve considerar impacto em reservas futuras.
        // Como remover a manutenção simplesmente "libera" o local, a integridade da reserva não é afetada negativamente.
        // Não há ação restritiva a tomar aqui para remover uma manutenção, a menos que houvesse
        // uma regra que dissesse que uma manutenção já iniciada não pode ser removida (mas não há essa RN).
        manutencaoRepository.remover(id);
    }

    @Override
    public List<ManutencaoLocal> listarManutencoesPorLocal(String localId) {
        // Valida se o local existe
        localRepository.buscarPorId(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local não encontrado."));

        // RN7: Consultar os períodos de manutenção cadastrados por local
        return manutencaoRepository.buscarPorLocalId(localId);
    }

    private void validarConflitoComReservas(String localId, LocalDateTime inicio, LocalDateTime fim) {
        List<ReservaLocal> reservasConflitantes = reservaLocalRepository.buscarReservasPorLocalEPeriodo(localId, inicio, fim);
        if (reservasConflitantes != null && !reservasConflitantes.isEmpty()) {
            throw new IllegalArgumentException("O período da manutenção conflita com reservas existentes.");
        }
    }
}

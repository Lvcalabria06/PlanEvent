package domain.conciliacao.repository;

import domain.conciliacao.entity.VinculoConciliacao;

import java.util.List;
import java.util.Optional;

public interface VinculoConciliacaoRepository {

    VinculoConciliacao salvar(VinculoConciliacao vinculo);

    Optional<VinculoConciliacao> buscarPorDespesaId(String despesaId);

    List<VinculoConciliacao> listarPorEventoId(String eventoId);

    List<VinculoConciliacao> listarPorContratoId(String contratoId);

    void removerPorDespesaId(String despesaId);
}

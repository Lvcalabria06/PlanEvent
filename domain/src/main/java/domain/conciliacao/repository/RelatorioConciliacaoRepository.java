package domain.conciliacao.repository;

import domain.conciliacao.entity.RelatorioConciliacao;

import java.util.Optional;

public interface RelatorioConciliacaoRepository {

    RelatorioConciliacao salvar(RelatorioConciliacao relatorio);

    Optional<RelatorioConciliacao> buscarPorId(String id);
}

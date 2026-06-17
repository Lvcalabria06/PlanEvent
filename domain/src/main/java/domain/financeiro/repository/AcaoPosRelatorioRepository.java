package domain.financeiro.repository;

import domain.financeiro.entity.AcaoPosRelatorio;

import java.util.List;
import java.util.Optional;

public interface AcaoPosRelatorioRepository {

    AcaoPosRelatorio salvar(AcaoPosRelatorio acao);

    Optional<AcaoPosRelatorio> buscarPorId(String id);

    List<AcaoPosRelatorio> listarPorRelatorioId(String relatorioId);
}

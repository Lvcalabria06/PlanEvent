package domain.equipe.repository;

import domain.equipe.entity.Equipe;

import java.util.List;
import java.util.Optional;

public interface EquipeRepository {
    Equipe salvar(Equipe equipe);
    Optional<Equipe> buscarPorId(String id);
    List<Equipe> listarPorEventoId(String eventoId);

    boolean existeEquipeComNomeNoEvento(String eventoId, String nome);
    boolean existeFuncionarioVinculado(String funcionarioId);
    boolean funcionarioJaEstaEmEquipeNoEvento(String funcionarioId, String eventoId);
}